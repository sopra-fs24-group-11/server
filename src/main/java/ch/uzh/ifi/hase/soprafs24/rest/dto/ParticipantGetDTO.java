package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class ParticipantGetDTO {
  private Long id;
  private String username;

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
}
