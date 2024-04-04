package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class ItemGetDTO {
  private boolean completed;

  private String item;

  private Long id;
  private Long participantId;

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

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getParticipantId() {
    return participantId;
  }

  public void setParticipantId(Long participantId) {
    this.participantId = participantId;
  }
}
