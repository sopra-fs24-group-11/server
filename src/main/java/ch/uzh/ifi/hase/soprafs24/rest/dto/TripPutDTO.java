package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;

public class TripPutDTO {
  // new admin missing - make id field or user field?
  private String tripName;

  private String tripDescription;

  private String temporaryMeetUpPlace;

  private String temporaryMeetUpCode;

  private LocalDateTime meetUpTime;

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

  public String getTemporaryMeetUpPlace() {
    return temporaryMeetUpPlace;
  }

  public void setTemporaryMeetUpPlace(String temporaryMeetUpPlace) {
    this.temporaryMeetUpPlace = temporaryMeetUpPlace;
  }

  public String getTemporaryMeetUpCode() {
    return temporaryMeetUpCode;
  }

  public void setTemporaryMeetUpCode(String temporaryMeetUpCode) {
    this.temporaryMeetUpCode = temporaryMeetUpCode;
  }

  public LocalDateTime getMeetUpTime() {
    return meetUpTime;
  }

  public void setMeetUpTime(LocalDateTime meetUpTime) {
    this.meetUpTime = meetUpTime;
  }

}
