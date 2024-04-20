package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Station;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import javax.transaction.Transactional;
import java.util.List;

@WebAppConfiguration
@SpringBootTest
@Transactional
@Rollback
public class ConnectionServiceIntegrationTest {

  @Autowired
  private ConnectionService connectionService;

  @Test
  public void getLocationsName_validInput_listOfStations() {
    List<Station> stations = ConnectionService.getLocationsName("Zurich");

    assertEquals("Zürich HB", stations.get(0).getStationName());
    assertEquals("8503000", stations.get(0).getStationCode());
  }
}
