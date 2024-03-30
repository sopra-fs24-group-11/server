package ch.uzh.ifi.hase.soprafs24.entity;

import javax.persistence.Column;

public class Station {
  @Column(nullable = false)
  private String stationName;
  @Column(nullable = false)
  private Integer stationCode;

  public String getStationName() {
    return this.stationName;
  }

  public void setStationName(String name) {
    this.stationName = name;
  }

  public Integer getStationCode() {
    return this.stationCode;
  }
  public void setStationCode(Integer code) {
    this.stationCode = code;
  }
}
