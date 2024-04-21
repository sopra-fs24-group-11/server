package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;

public class Friend {
  private Long friendId;
  private String username;
  private int points;
  private Double level;
  private FriendShipStatus status;

  public Long getFriendId() {
    return friendId;
  }

  public void setFriendId(Long friendId) {
    this.friendId = friendId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }

  public Double getLevel () {
    return level;
  }

  public void setLevel (Double level) {
    this.level = level;
  }

  public FriendShipStatus getStatus() {
    return status;
  }

  public void setStatus(FriendShipStatus status) {
    this.status = status;
  }
}
