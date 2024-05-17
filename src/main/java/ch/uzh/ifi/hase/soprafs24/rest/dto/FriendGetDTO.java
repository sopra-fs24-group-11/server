package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

public class FriendGetDTO {
  private Long friendId;
  private String username;
  private double points;
  private Double level;
  private UserStatus status;

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

  public double getPoints() {
    return points;
  }

  public void setPoints(double points) {
    this.points = points;
  }

  public Double getLevel () {
    return level;
  }

  public void setLevel (Double level) {
    this.level = level;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }
}
