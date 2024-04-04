package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class MemberGetDTO {
  private Long id;
  private String username;
  private byte[] profilePicture;

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

  public byte[] getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(byte[] profilePicture) {
    this.profilePicture = profilePicture;
  }
}
