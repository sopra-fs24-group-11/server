package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.Item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
public class GroupPackingItem extends Item implements Serializable {
  @Column(nullable=false)
  private boolean completed;

  @ManyToOne
  @JoinColumn(name = "selectedParticipant")
  private TripParticipant participant;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public TripParticipant getParticipant() {
    return participant;
  }

  public void setParticipant(TripParticipant participant) {
    this.participant = participant;
  }

  public Trip getTrip() {
    return trip;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }
}
