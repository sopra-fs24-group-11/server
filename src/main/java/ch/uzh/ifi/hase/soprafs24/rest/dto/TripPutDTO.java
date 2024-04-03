package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.Station;

import java.time.LocalDateTime;
import java.util.List;

public class TripPutDTO {

  private String tripName;

  private String tripDescription;

  private Station meetUpPlace;

  private LocalDateTime meetUpTime;

  private List<Long> participants;

  private int rating;

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

  public List<Long> getParticipants() {
    return participants;
  }

  public void setParticipants(List<Long> participants) {
    this.participants = participants;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }
}
