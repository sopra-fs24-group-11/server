package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Feedback implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "feedback_sequence")
  @SequenceGenerator(name = "feedback_sequence", sequenceName = "feedback_sequence", allocationSize = 1)
  private Long id;
  @Column(nullable = false)
  private Long userId;
  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String message;

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

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
