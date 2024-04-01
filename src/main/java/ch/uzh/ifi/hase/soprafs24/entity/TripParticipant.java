package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class TripParticipant implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  private Station startPoint;

  @Column(nullable = false)
  private LocalDateTime meetUpTime;

  @ManyToOne
  @JoinColumn(name = "invitator_id", nullable = false)
  private User invitator;

  @Column(nullable = false)
  private InvitationStatus status;

  @Column(nullable = false)
  private boolean favouriteTrip;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Station getStartPoint() {
    return startPoint;
  }

  public void setStartPoint(Station startPoint) {
    this.startPoint = startPoint;
  }

  public LocalDateTime getMeetUpTime() {
    return meetUpTime;
  }

  public void setMeetUpTime(LocalDateTime meetUpTime) {
    this.meetUpTime = meetUpTime;
  }

  public User getInvitator() {
    return invitator;
  }

  public void setInvitator(User invitator) {
    this.invitator = invitator;
  }

  public InvitationStatus getStatus() {
    return status;
  }

  public void setStatus(InvitationStatus status) {
    this.status = status;
  }

  public boolean isFavouriteTrip() {
    return favouriteTrip;
  }

  public void setFavouriteTrip(boolean favouriteTrip) {
    this.favouriteTrip = favouriteTrip;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Trip getTrip() {
    return trip;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }
}