package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Friendship implements Serializable {
  @Id
  @GeneratedValue
  private Long id;
  @ManyToOne
  @JoinColumn(nullable = false)
  private User friend1;

  @ManyToOne
  @JoinColumn(nullable = false)
  private User friend2;

  @Column(nullable = false)
  private int points;

  @Column(nullable = false)
  private FriendShipStatus status;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getFriend1() {
    return friend1;
  }

  public void setFriend1(User friend1) {
    this.friend1 = friend1;
  }

  public User getFriend2() {
    return friend2;
  }

  public void setFriend2(User friend2) {
    this.friend2 = friend2;
  }

  public FriendShipStatus getStatus() {
    return status;
  }

  public void setStatus(FriendShipStatus status) {
    this.status = status;
  }

  public int getPoints() {
    return points;
  }

  public void setPoints(int points) {
    this.points = points;
  }
}
