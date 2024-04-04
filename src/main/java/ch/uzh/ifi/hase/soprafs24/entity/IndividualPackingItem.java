package ch.uzh.ifi.hase.soprafs24.entity;


import javax.persistence.*;
import java.io.Serializable;

@Entity
public class IndividualPackingItem implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_sequence")
  @SequenceGenerator(name = "item_sequence", sequenceName = "item_sequence", allocationSize = 1)
  private Long id;

  @Column(nullable = false)
  private String item;


  @Column(nullable=false)
  private boolean completed;

  @ManyToOne
  @JoinColumn(name = "participant_id", nullable = false)
  private TripParticipant participant;

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
