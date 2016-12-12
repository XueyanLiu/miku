package com.biubiu.miku.util.video.player;

/**
 * Builds renderers for the player.
 */
public interface RendererBuilder {
  /**
   * Builds renderers for playback.
   *
   * @param player The player for which renderers are being built. {@link MediaPlayer#onRenderers}
   *          should be invoked once the renderers have been built. If building fails,
   *          {@link MediaPlayer#onRenderersError} should be invoked.
   */
  void buildRenderers(MediaPlayer player);

  /**
   * Cancels the current build operation, if there is one. Else does nothing.
   * <p>
   * A canceled build operation must not invoke {@link MediaPlayer#onRenderers} or
   * {@link MediaPlayer#onRenderersError} on the player, which may have been released.
   */
  void cancel();
}
