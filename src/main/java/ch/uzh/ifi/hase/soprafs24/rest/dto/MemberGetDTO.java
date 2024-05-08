package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class MemberGetDTO {
  private Long userId;
  private byte[] profilePicture;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public byte[] getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(byte[] profilePicture) {
    this.profilePicture = profilePicture;
  }
}
