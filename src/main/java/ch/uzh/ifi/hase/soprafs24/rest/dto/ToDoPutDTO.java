package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;

public class ToDoPutDTO {
  private boolean completed;

  private String item;

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item = item;
  }

  public boolean isCompleted() {
    return completed;
  }
  public void setCompleted(boolean completed) {
    this.completed = completed;
  }
}
