package ch.uzh.ifi.hase.soprafs24.entity;


import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class ParticipantConnection implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "participant_connection_sequence")
  @SequenceGenerator(name = "participant_connection_sequence", sequenceName = "participant_connection_sequence", allocationSize = 1)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "participant_id", nullable = false)
  private TripParticipant participant;

  @Column(nullable = false)
  private ConnectionType connectionType;

  @Column(nullable = false)
  private String connectionName;

  @Column(nullable = false)
  private LocalDateTime departureTime;

  @Embedded
  @AttributeOverride(name = "stationName", column = @Column(name = "departure_station_name", nullable = false))
  @AttributeOverride(name = "stationCode", column = @Column(name = "departure_station_code", nullable = false))
  private Station departurePoint;

  @Column(nullable = false)
  private LocalDateTime arrivalTime;

  @Embedded
  @AttributeOverride(name = "stationName", column = @Column(name = "arrival_station_name", nullable = false))
  @AttributeOverride(name = "stationCode", column = @Column(name = "arrival_station_code", nullable = false))
  private Station arrivalPoint;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public ConnectionType getConnectionType() {
    return connectionType;
  }

  public void setConnectionType(ConnectionType connectionType) {
    this.connectionType = connectionType;
  }

  public String getConnectionName() {
    return connectionName;
  }

  public void setConnectionName(String connectionName) {
    this.connectionName = connectionName;
  }

  public LocalDateTime getDepartureTime() {
    return departureTime;
  }

  public void setDepartureTime(LocalDateTime departureTime) {
    this.departureTime = departureTime;
  }

  public Station getDeparturePoint() {
    return departurePoint;
  }

  public void setDeparturePoint(Station departurePoint) {
    this.departurePoint = departurePoint;
  }

  public LocalDateTime getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(LocalDateTime arrivalTime) {
    this.arrivalTime = arrivalTime;
  }

  public Station getArrivalPoint() {
    return arrivalPoint;
  }

  public void setArrivalPoint(Station arrivalPoint) {
    this.arrivalPoint = arrivalPoint;
  }

  public TripParticipant getParticipant() {
    return participant;
  }
  public void setParticipant(TripParticipant participant) {
    this.participant = participant;
  }
}
