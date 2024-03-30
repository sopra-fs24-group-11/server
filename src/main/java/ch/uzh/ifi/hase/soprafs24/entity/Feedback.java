package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Feedback implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;
  @Column(nullable = false)
  private Long userId;
  @Column(nullable = false)
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
