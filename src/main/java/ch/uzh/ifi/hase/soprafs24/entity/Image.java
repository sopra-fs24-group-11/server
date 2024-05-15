package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Image implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_sequence")
  @SequenceGenerator(name = "image_sequence", sequenceName = "image_sequence", allocationSize = 1)
  private Long id;

  @Lob
  @Column(name = "image_data")
  private byte[] profilePicture;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  public byte[] getProfilePicture() {
    return profilePicture;
  }

  public void setProfilePicture(byte[] profilePicture) {
    this.profilePicture = profilePicture;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

}
