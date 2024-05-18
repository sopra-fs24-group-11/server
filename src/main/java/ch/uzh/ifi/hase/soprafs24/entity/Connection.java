package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Connection implements Serializable {
  private ConnectionType connectionType;
  private String connectionName;
  private LocalDateTime departureTime;
  private String departurePlatform;
  private Station departurePoint;
  private LocalDateTime arrivalTime;
  private String arrivalPlatform;
  private Station arrivalPoint;


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

  public String getDeparturePlatform() {
    return departurePlatform;
  }

  public void setDeparturePlatform(String departurePlatform) {
    this.departurePlatform = departurePlatform;
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

  public String getArrivalPlatform() {
    return arrivalPlatform;
  }

  public void setArrivalPlatform(String arrivalPlatform) {
    this.arrivalPlatform = arrivalPlatform;
  }

  public Station getArrivalPoint() {
    return arrivalPoint;
  }

  public void setArrivalPoint(Station arrivalPoint) {
    this.arrivalPoint = arrivalPoint;
  }


}
