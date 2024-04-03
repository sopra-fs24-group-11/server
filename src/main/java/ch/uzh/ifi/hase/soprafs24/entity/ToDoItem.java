package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.Item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class ToDoItem extends Item implements Serializable {
  @Column(nullable=false)
  private boolean completed;

  //@Column(name = "selectedParticipant", nullable = true)
  //private Long participantId;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  /*public Long getParticipant() {
    return participantId;
  }

  public void setParticipant(Long participantId) {
    this.participantId = participantId;
  }*/

  public Trip getTrip() {
    return trip;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }
}
