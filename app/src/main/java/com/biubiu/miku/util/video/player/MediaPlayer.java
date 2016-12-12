/*
 * Copyright (C) 2014 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.biubiu.miku.util.video.player;

import android.content.Context;
import android.media.MediaCodec.CryptoException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Surface;

import com.biubiu.miku.util.video.player.callbacks.SmoothStreamingTestMediaDrmCallback;
import com.biubiu.miku.util.video.player.callbacks.WidevineTestMediaDrmCallback;
import com.google.android.exoplayer.CodecCounters;
import com.google.android.exoplayer.DummyTrackRenderer;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer;
import com.google.android.exoplayer.MediaCodecTrackRenderer.DecoderInitializationException;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.TimeRange;
import com.google.android.exoplayer.TrackRenderer;
import com.google.android.exoplayer.audio.AudioCapabilities;
import com.google.android.exoplayer.audio.AudioTrack;
import com.google.android.exoplayer.chunk.ChunkSampleSource;
import com.google.android.exoplayer.chunk.Format;
import com.google.android.exoplayer.chunk.MultiTrackChunkSource;
import com.google.android.exoplayer.dash.DashChunkSource;
import com.google.android.exoplayer.drm.StreamingDrmSessionManager;
import com.google.android.exoplayer.hls.HlsSampleSource;
import com.google.android.exoplayer.metadata.MetadataTrackRenderer.MetadataRenderer;
import com.google.android.exoplayer.text.Cue;
import com.google.android.exoplayer.text.TextRenderer;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer.util.DebugTextViewHelper;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A wrapper around {@link ExoPlayer} that provides a higher level interface. It can be prepared
 * with one of a number of {@link RendererBuilder} classes to suit different use cases (e.g. DASH,
 * SmoothStreaming and so on).
 */
public class MediaPlayer
    implements
    ExoPlayer.Listener,
    ChunkSampleSource.EventListener,
    HlsSampleSource.EventListener,
    DefaultBandwidthMeter.EventListener,
    MediaCodecVideoTrackRenderer.EventListener,
    MediaCodecAudioTrackRenderer.EventListener,
    StreamingDrmSessionManager.EventListener,
    DashChunkSource.EventListener,
    TextRenderer,
    MetadataRenderer<Map<String, Object>>,
    DebugTextViewHelper.Provider {
  public static final int STATE_IDLE = ExoPlayer.STATE_IDLE;
  public static final int STATE_PREPARING = ExoPlayer.STATE_PREPARING;
  public static final int STATE_BUFFERING = ExoPlayer.STATE_BUFFERING;
  public static final int STATE_READY = ExoPlayer.STATE_READY;
  public static final int STATE_ENDED = ExoPlayer.STATE_ENDED;
  public static final int DISABLED_TRACK = -1;
  public static final int PRIMARY_TRACK = 0;
  public static final int RENDERER_COUNT = 4;
  public static final int TYPE_VIDEO = 0;
  public static final int TYPE_AUDIO = 1;
  public static final int TYPE_TEXT = 2;
  public static final int TYPE_METADATA = 3;
  private static final int RENDERER_BUILDING_STATE_IDLE = 1;
  private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
  private static final int RENDERER_BUILDING_STATE_BUILT = 3;
  private RendererBuilder rendererBuilder;
  private final ExoPlayer player;
  private final PlayerControl playerControl;
  private final Handler mainHandler;
  private final CopyOnWriteArrayList<Listener> listeners;
  private int rendererBuildingState;
  private int lastReportedPlaybackState;
  private boolean lastReportedPlayWhenReady;
  private Surface surface;
  private TrackRenderer videoRenderer;
  private CodecCounters codecCounters;
  private Format videoFormat;
  private int videoTrackToRestore;
  private BandwidthMeter bandwidthMeter;
  private MultiTrackChunkSource[] multiTrackSources;
  private String[][] trackNames;
  private int[] selectedTracks;
  private boolean backgrounded;
  private CaptionListener captionListener;
  private Id3MetadataListener id3MetadataListener;
  private InternalErrorListener internalErrorListener;
  private InfoListener infoListener;
  private Context context;
  private boolean isPrepared = false;

  public MediaPlayer(Context context) {
    this.context = context;
    player = ExoPlayer.Factory.newInstance(RENDERER_COUNT, 1000, 5000);
    player.addListener(this);
    playerControl = new PlayerControl(player);
    mainHandler = new Handler();
    listeners = new CopyOnWriteArrayList<>();
    lastReportedPlaybackState = STATE_IDLE;
    rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
    selectedTracks = new int[RENDERER_COUNT];
    // Disable text initially.
    selectedTracks[TYPE_TEXT] = DISABLED_TRACK;
  }

  public PlayerControl getPlayerControl() {
    return playerControl;
  }

  public void addListener(Listener listener) {
    listeners.add(listener);
  }

  public void removeListener(Listener listener) {
    listeners.remove(listener);
  }

  public void setInternalErrorListener(InternalErrorListener listener) {
    internalErrorListener = listener;
  }

  public void setInfoListener(InfoListener listener) {
    infoListener = listener;
  }

  public void setCaptionListener(CaptionListener listener) {
    captionListener = listener;
  }

  public void setMetadataListener(Id3MetadataListener listener) {
    id3MetadataListener = listener;
  }

  public void setSurface(Surface surface) {
    this.surface = surface;
    pushSurface(false);
  }

  public Surface getSurface() {
    return surface;
  }

  public void blockingClearSurface() {
    surface = null;
    pushSurface(true);
  }

  public int getTrackCount(int type) {
    return !player.getRendererHasMedia(type) ? 0 : trackNames[type].length;
  }

  public String getTrackName(int type, int index) {
    return trackNames[type][index];
  }

  public int getSelectedTrackIndex(int type) {
    return selectedTracks[type];
  }

  public void selectTrack(int type, int index) {
    if (selectedTracks[type] == index) {
      return;
    }
    selectedTracks[type] = index;
    pushTrackSelection(type, true);
    if (type == TYPE_TEXT && index == DISABLED_TRACK && captionListener != null) {
      captionListener.onCues(Collections.<Cue>emptyList());
    }
  }

  public void setBackgrounded(boolean backgrounded) {
    if (this.backgrounded == backgrounded) {
      return;
    }
    this.backgrounded = backgrounded;
    if (backgrounded) {
      videoTrackToRestore = getSelectedTrackIndex(TYPE_VIDEO);
      selectTrack(TYPE_VIDEO, DISABLED_TRACK);
      blockingClearSurface();
    } else {
      selectTrack(TYPE_VIDEO, videoTrackToRestore);
    }
  }

  public void setDataSource(String path) {
    setDataSource(path, Sample.Type.LOCAL);
  }

  public void setDataSource(String path, Sample.Type type) {
    setDataSource(path, type, null);
  }

  public void setDataSource(String path, Sample.Type type, String contentId) {
    setDataSource(path, type, contentId, null);
  }

  public void setDataSource(String path, Sample.Type type, String contentId,
                            AudioCapabilities audioCapabilities) {
    rendererBuilder = getRendererBuilder(context,
        new Sample.Builder().setUri(path).setType(type).setContentId(contentId)
            .setAudioCapabilities(audioCapabilities).build());
  }

  private RendererBuilder getRendererBuilder(Context context, Sample sample) {
    String userAgent = Util.getUserAgent(context, context.getApplicationInfo().name);
    switch (sample.getType()) {
      case SMOOTH_STREAMING:
        return new SmoothStreamingRendererBuilder(context, userAgent, sample.getUri(),
            new SmoothStreamingTestMediaDrmCallback());
      case DASH:
        return new DashRendererBuilder(context, userAgent, sample.getUri(),
            new WidevineTestMediaDrmCallback(sample.getContentId()), sample.getAudioCapabilities());
      case HLS:
        return new HlsRendererBuilder(context, userAgent, sample.getUri(),
            sample.getAudioCapabilities());
      case OTHER:
      case LOCAL:
        return new ExtractorRendererBuilder(context, userAgent, Uri.parse(sample.getUri()));
      default:
        throw new IllegalStateException("Unsupported type: " + sample.getType().toString());
    }
  }

  public void prepare() {
    if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT) {
      player.stop();
    }
    rendererBuilder.cancel();
    videoFormat = null;
    videoRenderer = null;
    multiTrackSources = null;
    rendererBuildingState = RENDERER_BUILDING_STATE_BUILDING;
    maybeReportPlayerState();
    rendererBuilder.buildRenderers(this);
  }

  /**
   * Invoked with the results from a {@link RendererBuilder}.
   *
   * @param trackNames        The names of the available tracks, indexed by {@link MediaPlayer} TYPE_*
   *                          constants. May be null if the track names are unknown. An individual element may be
   *                          null
   *                          if the track names are unknown for the corresponding type.
   * @param multiTrackSources Sources capable of switching between multiple available tracks,
   *                          indexed by {@link MediaPlayer} TYPE_* constants. May be null if there are no types
   *                          with
   *                          multiple tracks. An individual element may be null if it does not have multiple
   *                          tracks.
   * @param renderers         Renderers indexed by {@link MediaPlayer} TYPE_* constants. An individual
   *                          element may be null if there do not exist tracks of the corresponding type.
   * @param bandwidthMeter    Provides an estimate of the currently available bandwidth. May be null.
   */
  /* package */ void onRenderers(String[][] trackNames,
                                 MultiTrackChunkSource[] multiTrackSources, TrackRenderer[] renderers,
                                 BandwidthMeter bandwidthMeter) {
    // Normalize the results.
    if (trackNames == null) {
      trackNames = new String[RENDERER_COUNT][];
    }
    if (multiTrackSources == null) {
      multiTrackSources = new MultiTrackChunkSource[RENDERER_COUNT];
    }
    for (int rendererIndex = 0; rendererIndex < RENDERER_COUNT; rendererIndex++) {
      if (renderers[rendererIndex] == null) {
        // Convert a null renderer to a dummy renderer.
        renderers[rendererIndex] = new DummyTrackRenderer();
      }
      if (trackNames[rendererIndex] == null) {
        // Convert a null trackNames to an array of suitable length.
        int trackCount = multiTrackSources[rendererIndex] != null
            ? multiTrackSources[rendererIndex].getTrackCount()
            : 1;
        trackNames[rendererIndex] = new String[trackCount];
      }
    }
    // Complete preparation.
    this.trackNames = trackNames;
    this.videoRenderer = renderers[TYPE_VIDEO];
    this.codecCounters = videoRenderer instanceof MediaCodecTrackRenderer
        ? ((MediaCodecTrackRenderer) videoRenderer).codecCounters
        : renderers[TYPE_AUDIO] instanceof MediaCodecTrackRenderer
        ? ((MediaCodecTrackRenderer) renderers[TYPE_AUDIO]).codecCounters
        : null;
    this.multiTrackSources = multiTrackSources;
    this.bandwidthMeter = bandwidthMeter;
    pushSurface(false);
    pushTrackSelection(TYPE_VIDEO, true);
    pushTrackSelection(TYPE_AUDIO, true);
    pushTrackSelection(TYPE_TEXT, true);
    player.prepare(renderers);
    rendererBuildingState = RENDERER_BUILDING_STATE_BUILT;
  }

  /**
   * Invoked if a {@link RendererBuilder} encounters an error.
   *
   * @param e Describes the error.
   */
  /* package */ void onRenderersError(Exception e) {
    if (internalErrorListener != null) {
      internalErrorListener.onRendererInitializationError(e);
    }
    for (Listener listener : listeners) {
      listener.onError(e);
    }
    if (onErrorListener != null) {
      onErrorListener.onError(this, e);
    }
    rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
    maybeReportPlayerState();
  }

  public void setPlayWhenReady(boolean playWhenReady) {
    player.setPlayWhenReady(playWhenReady);
  }

  public void seekTo(long positionMs) {
    player.seekTo(positionMs);
  }

  public void release() {
    if (rendererBuilder != null) {
      rendererBuilder.cancel();
    }
    rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
    surface = null;
    player.release();
  }


  public int getPlaybackState() {
    if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILDING) {
      return STATE_PREPARING;
    }
    int playerState = player.getPlaybackState();
    if (rendererBuildingState == RENDERER_BUILDING_STATE_BUILT && playerState == STATE_IDLE) {
      // This is an edge case where the renderers are built, but are still being passed to the
      // player's playback thread.
      return STATE_PREPARING;
    }
    return playerState;
  }

  @Override
  public Format getFormat() {
    return videoFormat;
  }

  @Override
  public BandwidthMeter getBandwidthMeter() {
    return bandwidthMeter;
  }

  @Override
  public CodecCounters getCodecCounters() {
    return codecCounters;
  }

  @Override
  public long getCurrentPosition() {
    return player.getCurrentPosition();
  }

  public long getDuration() {
    return player.getDuration();
  }

  public int getBufferedPercentage() {
    return player.getBufferedPercentage();
  }

  public boolean getPlayWhenReady() {
    return player.getPlayWhenReady();
  }

  /* package */ Looper getPlaybackLooper() {
    return player.getPlaybackLooper();
  }

  /* package */ Handler getMainHandler() {
    return mainHandler;
  }

  @Override
  public void onPlayerStateChanged(boolean playWhenReady, int state) {
    maybeReportPlayerState();
  }

  @Override
  public void onPlayerError(ExoPlaybackException exception) {
    rendererBuildingState = RENDERER_BUILDING_STATE_IDLE;
    for (Listener listener : listeners) {
      listener.onError(exception);
    }
    if (onErrorListener != null) {
      onErrorListener.onError(this, exception);
    }
  }

  @Override
  public void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio) {
    for (Listener listener : listeners) {
      listener.onVideoSizeChanged(width, height, pixelWidthHeightRatio);
    }
    if (onVideoSizeChangedListener != null) {
      onVideoSizeChangedListener.onVideoSizeChanged(this, width, height, pixelWidthHeightRatio);
    }
  }

  @Override
  public void onDroppedFrames(int count, long elapsed) {
    if (infoListener != null) {
      infoListener.onDroppedFrames(count, elapsed);
    }
  }

  @Override
  public void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate) {
    if (infoListener != null) {
      infoListener.onBandwidthSample(elapsedMs, bytes, bitrateEstimate);
    }
  }

  @Override
  public void onDownstreamFormatChanged(int sourceId, Format format, int trigger, int mediaTimeMs) {
    if (infoListener == null) {
      return;
    }
    if (sourceId == TYPE_VIDEO) {
      videoFormat = format;
      infoListener.onVideoFormatEnabled(format, trigger, mediaTimeMs);
    } else if (sourceId == TYPE_AUDIO) {
      infoListener.onAudioFormatEnabled(format, trigger, mediaTimeMs);
    }
  }

  @Override
  public void onDrmSessionManagerError(Exception e) {
    if (internalErrorListener != null) {
      internalErrorListener.onDrmSessionManagerError(e);
    }
  }

  @Override
  public void onDecoderInitializationError(DecoderInitializationException e) {
    if (internalErrorListener != null) {
      internalErrorListener.onDecoderInitializationError(e);
    }
  }

  @Override
  public void onAudioTrackInitializationError(AudioTrack.InitializationException e) {
    if (internalErrorListener != null) {
      internalErrorListener.onAudioTrackInitializationError(e);
    }
  }

  @Override
  public void onAudioTrackWriteError(AudioTrack.WriteException e) {
    if (internalErrorListener != null) {
      internalErrorListener.onAudioTrackWriteError(e);
    }
  }

  @Override
  public void onCryptoError(CryptoException e) {
    if (internalErrorListener != null) {
      internalErrorListener.onCryptoError(e);
    }
  }

  @Override
  public void onDecoderInitialized(String decoderName, long elapsedRealtimeMs,
                                   long initializationDurationMs) {
    if (infoListener != null) {
      infoListener.onDecoderInitialized(decoderName, elapsedRealtimeMs, initializationDurationMs);
    }
  }

  @Override
  public void onLoadError(int sourceId, IOException e) {
    if (internalErrorListener != null) {
      internalErrorListener.onLoadError(sourceId, e);
    }
  }

  @Override
  public void onCues(List<Cue> cues) {
    if (captionListener != null && selectedTracks[TYPE_TEXT] != DISABLED_TRACK) {
      captionListener.onCues(cues);
    }
  }

  @Override
  public void onMetadata(Map<String, Object> metadata) {
    if (id3MetadataListener != null && selectedTracks[TYPE_METADATA] != DISABLED_TRACK) {
      id3MetadataListener.onId3Metadata(metadata);
    }
  }

  @Override
  public void onSeekRangeChanged(TimeRange seekRange) {
    if (infoListener != null) {
      infoListener.onSeekRangeChanged(seekRange);
    }
  }

  @Override
  public void onPlayWhenReadyCommitted() {
    // Do nothing.
  }

  @Override
  public void onDrawnToSurface(Surface surface) {
    // Do nothing.
  }

  @Override
  public void onLoadStarted(int sourceId, long length, int type, int trigger, Format format,
                            int mediaStartTimeMs, int mediaEndTimeMs) {
    if (infoListener != null) {
      infoListener.onLoadStarted(sourceId, length, type, trigger, format, mediaStartTimeMs,
          mediaEndTimeMs);
    }
  }

  @Override
  public void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format,
                              int mediaStartTimeMs, int mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs) {
    if (infoListener != null) {
      infoListener.onLoadCompleted(sourceId, bytesLoaded, type, trigger, format, mediaStartTimeMs,
          mediaEndTimeMs, elapsedRealtimeMs, loadDurationMs);
    }
  }

  @Override
  public void onLoadCanceled(int sourceId, long bytesLoaded) {
    // Do nothing.
  }

  @Override
  public void onUpstreamDiscarded(int sourceId, int mediaStartTimeMs, int mediaEndTimeMs) {
    // Do nothing.
  }

  private void maybeReportPlayerState() {
    boolean playWhenReady = player.getPlayWhenReady();
    int playbackState = getPlaybackState();
    if (lastReportedPlayWhenReady != playWhenReady || lastReportedPlaybackState != playbackState) {
      for (Listener listener : listeners) {
        listener.onStateChanged(playWhenReady, playbackState);
      }
      switch (playbackState) {
        case ExoPlayer.STATE_IDLE:
          break;
        case ExoPlayer.STATE_PREPARING:
          break;
        case ExoPlayer.STATE_READY:
          if (onPreparedListener != null && !isPrepared) {
            isPrepared = true;
            onPreparedListener.onPrepared(this);
          }
          break;
        case ExoPlayer.STATE_BUFFERING:
          break;
        case ExoPlayer.STATE_ENDED:
          if (playWhenReady && onCompletionListener != null) {
            onCompletionListener.onCompletion(this);
          }
          break;
      }
      lastReportedPlayWhenReady = playWhenReady;
      lastReportedPlaybackState = playbackState;
    }
  }

  private void pushSurface(boolean blockForSurfacePush) {
    if (videoRenderer == null) {
      return;
    }
    if (blockForSurfacePush) {
      player.blockingSendMessage(
          videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
    } else {
      player.sendMessage(
          videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
    }
  }

  private void pushTrackSelection(int type, boolean allowRendererEnable) {
    if (multiTrackSources == null) {
      return;
    }
    int trackIndex = selectedTracks[type];
    if (trackIndex == DISABLED_TRACK) {
      player.setRendererEnabled(type, false);
    } else if (multiTrackSources[type] == null) {
      player.setRendererEnabled(type, allowRendererEnable);
    } else {
      boolean playWhenReady = player.getPlayWhenReady();
      player.setPlayWhenReady(false);
      player.setRendererEnabled(type, false);
      player.sendMessage(multiTrackSources[type], MultiTrackChunkSource.MSG_SELECT_TRACK,
          trackIndex);
      player.setRendererEnabled(type, allowRendererEnable);
      player.setPlayWhenReady(playWhenReady);
    }
  }

  public void setPlaybackSpeed(float speed) {
    if (player != null) {
      player.setPlaybackSpeed(speed);
    }
  }

  public void setVolume(float volume) {
    if (player != null) {
      player.setVolume(volume);
    }
  }

  public boolean isPlaying() {
    if (player != null) {
      return player.getPlaybackState() > ExoPlayer.STATE_PREPARING && player.getPlayWhenReady();
    }
    return false;
  }

  public void stop() {
    if (player != null) {
      player.stop();
    }
  }

  public void pause() {
    if (player != null) {
      player.setPlayWhenReady(false);
    }
  }

  public void start() {
    if (player != null) {
      player.setPlayWhenReady(true);
    }
  }

  public void setMute(boolean toMute) {
    if (toMute) {
      selectTrack(TYPE_AUDIO, -1);
    } else {
      selectTrack(TYPE_AUDIO, 0);
    }
  }

  /**
   * A listener for core events.
   */
  public interface Listener {
    void onStateChanged(boolean playWhenReady, int playbackState);

    void onError(Exception e);

    void onVideoSizeChanged(int width, int height, float pixelWidthHeightRatio);
  }

  /**
   * A listener for internal errors.
   * <p>
   * These errors are not visible to the user, and hence this listener is provided for
   * informational purposes only. Note however that an internal error may cause a fatal
   * error if the player fails to recover. If this happens, {@link Listener#onError(Exception)}
   * will be invoked.
   */
  public interface InternalErrorListener {
    void onRendererInitializationError(Exception e);

    void onAudioTrackInitializationError(AudioTrack.InitializationException e);

    void onAudioTrackWriteError(AudioTrack.WriteException e);

    void onDecoderInitializationError(DecoderInitializationException e);

    void onCryptoError(CryptoException e);

    void onLoadError(int sourceId, IOException e);

    void onDrmSessionManagerError(Exception e);
  }

  /**
   * A listener for debugging information.
   */
  public interface InfoListener {
    void onVideoFormatEnabled(Format format, int trigger, int mediaTimeMs);

    void onAudioFormatEnabled(Format format, int trigger, int mediaTimeMs);

    void onDroppedFrames(int count, long elapsed);

    void onBandwidthSample(int elapsedMs, long bytes, long bitrateEstimate);

    void onLoadStarted(int sourceId, long length, int type, int trigger, Format format,
                       int mediaStartTimeMs, int mediaEndTimeMs);

    void onLoadCompleted(int sourceId, long bytesLoaded, int type, int trigger, Format format,
                         int mediaStartTimeMs, int mediaEndTimeMs, long elapsedRealtimeMs, long loadDurationMs);

    void onDecoderInitialized(String decoderName, long elapsedRealtimeMs,
                              long initializationDurationMs);

    void onSeekRangeChanged(TimeRange seekRange);
  }

  /**
   * Interface definition for a callback to be invoked when the media
   * source is ready for playback.
   */
  public interface OnPreparedListener {
    /**
     * Called when the media file is ready for playback.
     *
     * @param mp the MediaPlayer that is ready for playback
     */
    void onPrepared(MediaPlayer mp);
  }

  /**
   * Register a callback to be invoked when the media source is ready
   * for playback.
   *
   * @param listener the callback that will be run
   */
  public void setOnPreparedListener(OnPreparedListener listener) {
    onPreparedListener = listener;
  }

  private OnPreparedListener onPreparedListener;

  /**
   * Interface definition for a callback to be invoked when playback of
   * a media source has completed.
   */
  public interface OnCompletionListener {
    /**
     * Called when the end of a media source is reached during playback.
     *
     * @param mp the MediaPlayer that reached the end of the file
     */
    void onCompletion(MediaPlayer mp);
  }

  /**
   * Register a callback to be invoked when the end of a media source
   * has been reached during playback.
   *
   * @param listener the callback that will be run
   */
  public void setOnCompletionListener(OnCompletionListener listener) {
    onCompletionListener = listener;
  }

  private OnCompletionListener onCompletionListener;

  /**
   * Interface definition of a callback to be invoked when the
   * video size is first known or updated
   */
  public interface OnVideoSizeChangedListener {
    /**
     * Called to indicate the video size
     * <p>
     * The video size (width and height) could be 0 if there was no video,
     * no display surface was set, or the value was not determined yet.
     *
     * @param mp                    the MediaPlayer associated with this callback
     * @param width                 the width of the video
     * @param height                the height of the video
     * @param pixelWidthHeightRatio
     */
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height,
                                   float pixelWidthHeightRatio);
  }

  /**
   * Register a callback to be invoked when the video size is
   * known or updated.
   *
   * @param listener the callback that will be run
   */
  public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener listener) {
    onVideoSizeChangedListener = listener;
  }

  private OnVideoSizeChangedListener onVideoSizeChangedListener;

  public interface OnErrorListener {
    boolean onError(MediaPlayer mp, Exception e);
  }

  /**
   * Register a callback to be invoked when an error has happened
   * during an asynchronous operation.
   *
   * @param listener the callback that will be run
   */
  public void setOnErrorListener(OnErrorListener listener) {
    onErrorListener = listener;
  }

  private OnErrorListener onErrorListener;

  /**
   * A listener for receiving notifications of timed text.
   */
  public interface CaptionListener {
    void onCues(List<Cue> cues);
  }

  /**
   * A listener for receiving ID3 metadata parsed from the media stream.
   */
  public interface Id3MetadataListener {
    void onId3Metadata(Map<String, Object> metadata);
  }

}
