package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class ConnectionAndUserDTO {
  private String username;
  private List<ConnectionDTO> connectionDTO;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<ConnectionDTO> getConnectionDTO() {
    return connectionDTO;
  }

  public void setConnectionDTO(List<ConnectionDTO> connectionDTO) {
    this.connectionDTO = connectionDTO;
  }


}
