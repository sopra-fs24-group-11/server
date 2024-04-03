package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.Item;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class ToDoItem implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_sequence")
  @SequenceGenerator(name = "item_sequence", sequenceName = "item_sequence", allocationSize = 1)
  private Long id;

  @Column(nullable = true)
  private String item;


  @Column(nullable=false)
  private boolean completed;

  @Column(name = "selectedParticipant", nullable = true)
  private Long participantId;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

  public Long getParticipant() {
    return participantId;
  }

  public void setParticipant(Long participantId) {
    this.participantId = participantId;
  }

  public Trip getTrip() {
    return trip;
  }

  public void setTrip(Trip trip) {
    this.trip = trip;
  }
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getItem() {
    return item;
  }

  public void setItem(String item) {
    this.item = item;
  }
}
