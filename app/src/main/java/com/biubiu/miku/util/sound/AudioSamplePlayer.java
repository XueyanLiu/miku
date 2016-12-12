package com.biubiu.miku.util.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.nio.ShortBuffer;

public class AudioSamplePlayer {
  public interface OnCompletionListener {
    void onCompletion();
  }

  private ShortBuffer mSamples;
  private int mSampleRate;
  private int mChannels;
  private int mNumSamples; // Number of samples per channel.
  private AudioTrack mAudioTrack;
  private short[] mBuffer;
  private int mPlaybackStart; // Start offset, in samples.
  private Thread mPlayThread;
  private boolean mKeepPlaying;
  private OnCompletionListener mListener;
  private float gain = 1;

  public AudioSamplePlayer(ShortBuffer samples, int sampleRate, int channels, int numSamples) {
    mSamples = samples;
    mSampleRate = sampleRate;
    mChannels = channels;
    mNumSamples = numSamples;
    mPlaybackStart = 0;

    int bufferSize = AudioTrack.getMinBufferSize(
        mSampleRate,
        mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT);
    // make sure minBufferSize can contain at least 1 second of audio (16 bits sample).
    if (bufferSize < mChannels * mSampleRate * 2) {
      bufferSize = mChannels * mSampleRate * 2;
    }
    mBuffer = new short[bufferSize / 2]; // bufferSize is in Bytes.
    mAudioTrack = new AudioTrack(
        AudioManager.STREAM_MUSIC,
        mSampleRate,
        mChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
        AudioFormat.ENCODING_PCM_16BIT,
        mBuffer.length * 2,
        AudioTrack.MODE_STREAM);
    // Check when player played all the given data and notify user if mListener is set.
    mAudioTrack.setNotificationMarkerPosition(mNumSamples - 1); // Set the marker to the end.
    mAudioTrack.setPlaybackPositionUpdateListener(
        new AudioTrack.OnPlaybackPositionUpdateListener() {
          @Override
          public void onPeriodicNotification(AudioTrack track) {}

          @Override
          public void onMarkerReached(AudioTrack track) {
            stop();
            if (mListener != null) {
              mListener.onCompletion();
            }
          }
        });
    mPlayThread = null;
    mKeepPlaying = true;
    mListener = null;
  }

  public AudioSamplePlayer(SoundFile sf) {
    this(sf.getSamples(), sf.getSampleRate(), sf.getChannels(), sf.getNumSamples());
  }

  public void setOnCompletionListener(OnCompletionListener listener) {
    mListener = listener;
  }

  public boolean isPlaying() {
    return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING;
  }

  public boolean isPaused() {
    return mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED;
  }

  public void start() {
    if (isPlaying()) {
      return;
    }
    mKeepPlaying = true;
    mAudioTrack.flush();
    mAudioTrack.setStereoVolume(gain, gain);
    mAudioTrack.play();
    mAudioTrack.setPlaybackRate(mAudioTrack.getSampleRate() );
    // Setting thread feeding the audio samples to the audio hardware.
    // (Assumes mChannels = 1 or 2).
    mPlayThread = new Thread() {
      public void run() {
        int position = mPlaybackStart * mChannels;
        try {
          mSamples.position(position);
          int limit = mNumSamples * mChannels;
          while (mSamples.position() < limit && mKeepPlaying) {
            int numSamplesLeft = limit - mSamples.position();
            if (numSamplesLeft >= mBuffer.length) {
              mSamples.get(mBuffer);
            } else {
              for (int i = numSamplesLeft; i < mBuffer.length; i++) {
                mBuffer[i] = 0;
              }
              mSamples.get(mBuffer, 0, numSamplesLeft);
            }
            // TODO(nfaralli): use the write method that takes a ByteBuffer as argument.
            mAudioTrack.write(mBuffer, 0, mBuffer.length);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    mPlayThread.start();
  }

  public void pause() {
    if (isPlaying()) {
      mAudioTrack.pause();
      // mAudioTrack.write() should block if it cannot write.
    }
  }

  public void stop() {
    if (isPlaying() || isPaused()) {
      mKeepPlaying = false;
      mAudioTrack.pause(); // pause() stops the playback immediately.
      mAudioTrack.stop(); // Unblock mAudioTrack.write() to avoid deadlocks.
      if (mPlayThread != null) {
        try {
          mPlayThread.join();
        } catch (InterruptedException e) {}
        mPlayThread = null;
      }
      mAudioTrack.flush(); // just in case...
    }
  }

  public void release() {
    stop();
    mAudioTrack.release();
  }

  public void seekTo(int msec) {
    boolean wasPlaying = isPlaying();
    stop();
    mPlaybackStart = (int) (msec * (mSampleRate / 1000.0));
    if (mPlaybackStart > mNumSamples) {
      mPlaybackStart = mNumSamples; // Nothing to play...
    }
    mAudioTrack.setStereoVolume(gain, gain);
    mAudioTrack.setNotificationMarkerPosition(mNumSamples - 1 - mPlaybackStart);
    if (wasPlaying) {
      start();
    }
  }

  public void setVolume(float gain) {
    this.gain = gain;
    if (mAudioTrack != null) {
      mAudioTrack.setStereoVolume(gain, gain);
    }
  }

  public int getCurrentPosition() {
    return (int) ((mPlaybackStart + mAudioTrack.getPlaybackHeadPosition()) * (1000.0 / mSampleRate));
  }
}
