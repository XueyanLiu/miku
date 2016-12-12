package com.biubiu.miku.util.video.player;

import com.google.android.exoplayer.audio.AudioCapabilities;

import java.util.Locale;

public class Sample {
  public final String name;
  public final String contentId;
  public final String uri;
  public final Type type;
  private AudioCapabilities audioCapabilities;

  public Sample(String name, String uri, Type type) {
    this(name, name.toLowerCase(Locale.US).replaceAll("\\s", ""), uri, type);
  }

  public Sample(String name, String contentId, String uri, Type type) {
    this.name = name;
    this.contentId = contentId;
    this.uri = uri;
    this.type = type;
  }

  public Sample(String name, String contentId, String uri, Type type,
                AudioCapabilities audioCapabilities) {
    this.name = name;
    this.contentId = contentId;
    this.uri = uri;
    this.type = type;
    this.audioCapabilities = audioCapabilities;
  }

  public String getName() {
    return name;
  }

  public String getContentId() {
    return contentId;
  }

  public String getUri() {
    return uri;
  }

  public Type getType() {
    return type;
  }

  public void setAudioCapabilities(AudioCapabilities audioCapabilities) {
    this.audioCapabilities = audioCapabilities;
  }

  public AudioCapabilities getAudioCapabilities() {
    return audioCapabilities;
  }

  public static class Builder {
    private String name;
    private String contentId;
    private String uri;
    private Type type;
    private AudioCapabilities audioCapabilities;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setContentId(String contentId) {
      this.contentId = contentId;
      return this;
    }

    public Builder setUri(String uri) {
      this.uri = uri;
      return this;
    }

    public Builder setType(Type type) {
      this.type = type;
      return this;
    }

    public Builder setAudioCapabilities(AudioCapabilities audioCapabilities) {
      this.audioCapabilities = audioCapabilities;
      return this;
    }

    public Sample build() {
      return new Sample(name, contentId, uri, type, audioCapabilities);
    }
  }

  public enum Type {
    SMOOTH_STREAMING, LOCAL, HLS, DASH, OTHER
  }
}
