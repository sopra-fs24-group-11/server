package ch.uzh.ifi.hase.soprafs24.constant;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public abstract class Item implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_sequence")
  @SequenceGenerator(name = "notification_sequence", sequenceName = "notification_sequence", allocationSize = 1)
  private Long id;

  @Column(nullable = false)
  private String item;

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
