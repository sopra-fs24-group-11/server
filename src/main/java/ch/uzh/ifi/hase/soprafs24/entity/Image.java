package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Embeddable;
import javax.persistence.Lob;
import javax.persistence.Column;
import java.io.Serializable;

@Embeddable
public class Image  implements Serializable {
  @Lob
  @Column(name = "image_data", columnDefinition = "BLOB")
  private byte[] profilePicture;


  public byte[] getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(byte[] profilePicture) {
    this.profilePicture = profilePicture;
  }
}
