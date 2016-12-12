package com.biubiu.miku.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable {
  private static final long serialVersionUID = 1136254727538559621L;
  @SerializedName("avatar")
  private String avatarUrl;
  @SerializedName("name")
  private String name;
  @SerializedName("id")
  private String id;
  @SerializedName("followerCount")
  private int followerCount;
  @SerializedName("followingCount")
  private int followedCount;
  @SerializedName("favCount")
  private int favCount;
  @SerializedName("following")
  private boolean isFollowing;
  @SerializedName("followed")
  private boolean isFollowed;
  @SerializedName("postCount")
  private int postCount;
  @SerializedName("block")
  private boolean block;

  // temp 测试
  public User(String avatarUrl, String name, String id, int followerCount, int followedCount,
              int favCount, boolean isFollowing, int postCount, boolean isFollowed, boolean block) {
    this.avatarUrl = avatarUrl;
    this.name = name;
    this.id = id;
    this.followerCount = followerCount;
    this.followedCount = followedCount;
    this.favCount = favCount;
    this.isFollowing = isFollowing;
    this.isFollowed = isFollowed;
    this.postCount = postCount;
    this.block = block;
  }

  public void setPostCount(int postCount) {
    this.postCount = postCount;
  }

  public void setFollowerCount(int followerCount) {
    this.followerCount = followerCount;
  }

  public void setIsFollowing(boolean isFollowing) {
    this.isFollowing = isFollowing;
  }

  public void setFollowedCount(int followedCount) {
    this.followedCount = followedCount;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setFavCount(int favCount) {
    this.favCount = favCount;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }


  public int getFollowerCount() {
    return followerCount;
  }

  public int getFollowedCount() {
    return followedCount;
  }

  public int getFavoriteCount() {
    return favCount;
  }

  public boolean isFollowing() {
    return isFollowing;
  }

  public boolean isFollowed() {
    return isFollowed;
  }

  public int getPostCount() {
    return postCount;
  }

  public boolean isBlock() {
    return block;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}
