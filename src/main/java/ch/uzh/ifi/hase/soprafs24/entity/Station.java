package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Column;

public class Station {
  @Column(nullable = false)
  private String stationName;
  @Column(nullable = false)
  private String stationCode;

  public String getStationName() {
    return this.stationName;
  }

  public void setStationName(String name) {
    this.stationName = name;
  }

  public String getStationCode() {
    return this.stationCode;
  }
  public void setStationCode(String code) {
    this.stationCode = code;
  }
}
