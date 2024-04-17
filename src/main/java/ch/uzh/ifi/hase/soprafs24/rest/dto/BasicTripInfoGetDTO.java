package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.Station;

import java.time.LocalDateTime;

public class BasicTripInfoGetDTO {
  private Long id;
  private String tripName;

  private String tripDescription;

  private int numberOfParticipants;

  private Station meetUpPlace;

  private LocalDateTime meetUpTime;

  private boolean completed;

  private int rating;

  private boolean favourite;

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


  public int getNumberOfParticipants() {
    return numberOfParticipants;
  }

  public void setNumberOfParticipants(int numberOfParticipants) {
    this.numberOfParticipants = numberOfParticipants;
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

  public void setFavourite(boolean favourite) {this.favourite = favourite;}

  public boolean isFavourite() {return favourite;}
}
