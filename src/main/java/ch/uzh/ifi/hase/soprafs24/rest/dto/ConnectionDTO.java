package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.entity.Station;

import java.time.LocalDateTime;

public class ConnectionDTO {
    private ConnectionType connectionType;
    private String connectionName;
    private LocalDateTime departureTime;
    private Station departurePoint;
    private LocalDateTime arrivalTime;
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


}
