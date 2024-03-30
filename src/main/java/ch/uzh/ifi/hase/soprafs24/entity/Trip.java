package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Trip implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue
  private Long id;

  @Column(nullable = false)
  private String tripName;

  @Column(nullable = false)
  private String tripDescription;

  @ManyToOne
  @JoinColumn(name = "administrator_id", nullable = false)
  private User administrator;

  @Column(nullable = false)
  private int numberOfParticipants;

  @Column(nullable = false)
  private int maxParticipants;

  @Embedded
  private Station meetUpPlace;

  @Column(nullable = false)
  private LocalDateTime meetUpTime;

  @Column(nullable = false)
  private boolean completed;

  @Column(nullable = false)
  private int rating;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTripName() {
    return tripName;
  }

  public void setTripName(String tripName) {
    this.tripName = tripName;
  }

  public String getTripDescription() {
    return tripDescription;
  }

  public void setTripDescription(String tripDescription) {
    this.tripDescription = tripDescription;
  }

  public User getAdministrator() {
    return administrator;
  }

  public void setAdministrator(User administrator) {
    this.administrator = administrator;
  }

  public int getNumberOfParticipants() {
    return numberOfParticipants;
  }

  public void setNumberOfParticipants(int numberOfParticipants) {
    this.numberOfParticipants = numberOfParticipants;
  }

  public int getMaxParticipants() {
    return maxParticipants;
  }

  public void setMaxParticipants(int maxParticipants) {
    this.maxParticipants = maxParticipants;
  }

  public Station getMeetUpPlace() {
    return meetUpPlace;
  }

  public void setMeetUpPlace(Station meetUpPlace) {
    this.meetUpPlace = meetUpPlace;
  }

  public LocalDateTime getMeetUpTime() {
    return meetUpTime;
  }

  public void setMeetUpTime(LocalDateTime meetUpTime) {
    this.meetUpTime = meetUpTime;
  }

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }
}
