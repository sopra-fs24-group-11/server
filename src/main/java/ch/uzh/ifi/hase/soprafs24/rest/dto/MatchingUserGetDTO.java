package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;

public class MatchingUserGetDTO {
  private Long id;
  private String username;
  private Double level;

  private FriendshipStatusSearch friendshipStatus;
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Double getLevel () {
    return level;
  }

  public void setLevel (Double level) {
    this.level = level;
  }

  public FriendshipStatusSearch getStatus () {
    return friendshipStatus;
  }

  public void setStatus (FriendshipStatusSearch friendshipStatus) {
    this.friendshipStatus = friendshipStatus;
  }
}
