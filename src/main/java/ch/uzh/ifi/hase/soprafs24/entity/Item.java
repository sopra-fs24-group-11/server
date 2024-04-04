package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.ItemType;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Item implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_sequence")
  @SequenceGenerator(name = "item_sequence", sequenceName = "item_sequence", allocationSize = 1)
  private Long id;

  @Column(nullable = true)
  private String item;


  @Column(nullable=false)
  private boolean completed;

  @ManyToOne
  @JoinColumn(name = "participant", nullable = true)
  private TripParticipant participant;

  @Column(name = "user_id", nullable = true)
  private Long userId;

  @ManyToOne
  @JoinColumn(name = "trip", nullable = false)
  private Trip trip;

  @Column (name = "itemType", nullable = false)
  private ItemType itemType;

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
  // not sure if the userId can be handled like this
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
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

  public ItemType getItemType() {return itemType; }

  public void setItemType(ItemType itemType) {this.itemType = itemType; }
}
