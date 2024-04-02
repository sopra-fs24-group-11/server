package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TripPostDTO {

  private String tripName;

  private String tripDescription;

  private String temporaryMeetUpPlace;

  private String temporaryMeetUpCode;

  private LocalDateTime meetUpTime;

  private List<Long> participants;

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

  public void setTemporaryMeetUpPlace(String meetUpPlace) {
    this.temporaryMeetUpPlace = temporaryMeetUpPlace;
  }

  public String getTemporaryMeetUpCode() {
    return temporaryMeetUpCode;
  }

  public void setTemporaryMeetUpCode(String meetUpCode) {
    this.temporaryMeetUpCode = temporaryMeetUpCode;
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

}
