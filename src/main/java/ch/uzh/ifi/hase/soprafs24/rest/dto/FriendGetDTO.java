package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class FriendGetDTO {
  private Long friendId;
  private String username;
  private int points;
  private Double level;

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
}
