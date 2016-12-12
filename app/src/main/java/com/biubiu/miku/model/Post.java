package com.biubiu.miku.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Post implements Serializable {
  private static final long serialVersionUID = -8102782601425923485L;
  private PostType postType = PostType.NORMAL;
  private String uploadKey;
  @SerializedName("video")
  private String videoUrl;
  @SerializedName("cover")
  private String coverUrl;
  private String localCoverPath;
  private String id;
  @SerializedName("author")
  private User user;
  @SerializedName("createTime")
  private long createTime;
  @SerializedName("favCount")
  private int likeCount;
  @SerializedName("commentCount")
  private int commentCount;
  @SerializedName("duration")
  private int duration;
  @SerializedName("faved")
  private boolean isFaved;
  @SerializedName("mine")
  private boolean isMine;
  private int width;
  private int height;
  private int quality;
  @SerializedName("highVideo")
  private Video highVideo;
  @SerializedName("mediuemVideo")
  private Video mediuemVideo;
  @SerializedName("lowVideo")
  private Video lowVideo;
  private double percent;//发送使用
  private float videoRatioWH;

  public Post(PostType postType, String uploadKey, String videoUrl, String coverUrl, String id,
              User user, long createTime, int likeCount, int commentCount, int duration, boolean isFaved,
              boolean isMine, String localCoverPath, int width, int height, int quality) {
    this.postType = postType;
    this.uploadKey = uploadKey;
    this.videoUrl = videoUrl;
    this.coverUrl = coverUrl;
    this.id = id;
    this.user = user;
    this.createTime = createTime;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
    this.duration = duration;
    this.isFaved = isFaved;
    this.isMine = isMine;
    this.localCoverPath = localCoverPath;
    this.width = width;
    this.height = height;
    this.quality = quality;
  }

  public Post() {
  }

  public void setPostType(PostType postType) {
    this.postType = postType;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public void setCoverUrl(String coverUrl) {
    this.coverUrl = coverUrl;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public void setIsMine(boolean isMine) {
    this.isMine = isMine;
  }

  public String getId() {
    return id;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public User getUser() {
    return user;
  }

  public long getCreateTime() {
    return createTime;
  }

  public String getCoverUrl() {
    return coverUrl;
  }

  public int getCommentCount() {
    return commentCount;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public int getDuration() {
    return duration;
  }

  public boolean isFaved() {
    return isFaved;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public void setIsFaved(boolean isFaved) {
    this.isFaved = isFaved;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public PostType getPostType() {
    return postType == null ? PostType.NORMAL : postType;
  }

  public void setUploadKey(String uploadKey) {
    this.uploadKey = uploadKey;
  }

  public String getUploadKey() {
    return uploadKey;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public boolean isMine() {
    return isMine;
  }

  public void setLocalCoverPath(String localCoverPath) {
    this.localCoverPath = localCoverPath;
  }

  public String getLocalCoverPath() {
    return localCoverPath;
  }

  public int getHeight() {
    return height;
  }

  public int getQuality() {
    return quality;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public void setQuality(int quality) {
    this.quality = quality;
  }

  public Video getHighVideo() {
    return highVideo;
  }

  public Video getMediuemVideo() {
    return mediuemVideo;
  }

  public Video getLowVideo() {
    return lowVideo;
  }

  public double getPercent() {
    return percent;
  }

  public void setPercent(double percent) {
    this.percent = percent;
  }

  public float getVideoRatioWH() {
    return videoRatioWH;
  }

  public void setVideoRatioWH(float videoRatioWH) {
    this.videoRatioWH = videoRatioWH;
  }

  public enum PostType {
    SENDING, NORMAL, FAILED
  }


  public static class Builder {
    PostType postType;
    String uploadKey;
    String videoUrl;
    String coverUrl;
    String localCoverPath;
    String id;
    User user;
    long createTime;
    int likeCount;
    int commentCount;
    int duration;
    boolean isFaved;
    boolean isMine;
    int width;
    int height;
    int quality;

    public Builder setPostType(PostType postType) {
      this.postType = postType;
      return this;
    }

    public Builder setUploadKey(String uploadKey) {
      this.uploadKey = uploadKey;
      return this;
    }

    public Builder setVideoUrl(String videoUrl) {
      this.videoUrl = videoUrl;
      return this;
    }

    public Builder setCoverUrl(String coverUrl) {
      this.coverUrl = coverUrl;
      return this;
    }

    public Builder setId(String id) {
      this.id = id;
      return this;
    }

    public Builder setUser(User user) {
      this.user = user;
      return this;
    }

    public Builder setCreateTime(long createTime) {
      this.createTime = createTime;
      return this;
    }

    public Builder setLikeCount(int likeCount) {
      this.likeCount = likeCount;
      return this;
    }

    public Builder setCommentCount(int commentCount) {
      this.commentCount = commentCount;
      return this;
    }

    public Builder setDuration(int duration) {
      this.duration = duration;
      return this;
    }

    public Builder setIsFaved(boolean isFaved) {
      this.isFaved = isFaved;
      return this;
    }

    public Builder setIsMine(boolean isMine) {
      this.isMine = isMine;
      return this;
    }

    public Builder setLocalCoverPath(String localCoverPath) {
      this.localCoverPath = localCoverPath;
      return this;
    }

    public Builder setWidth(int width) {
      this.width = width;
      return this;
    }

    public Builder setHeight(int height) {
      this.height = height;
      return this;
    }

    public Builder setQuality(int quality) {
      this.quality = quality;
      return this;
    }

    public Post build() {
      return new Post(postType, uploadKey, videoUrl, coverUrl, id, user, createTime, likeCount,
          commentCount, duration, isFaved, isMine, localCoverPath, width, height, quality);
    }
  }
}
