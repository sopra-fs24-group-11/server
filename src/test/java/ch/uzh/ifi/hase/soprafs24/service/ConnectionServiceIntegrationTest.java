package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebAppConfiguration
@SpringBootTest
class ConnectionServiceIntegrationTest {



  @Qualifier("participantConnectionRepository")
  @Autowired
  private ParticipantConnectionRepository participantConnectionRepository;

  @Qualifier("tripRepository")
  @Autowired
  private TripRepository tripRepository;

  @Qualifier("tripParticipantRepository")
  @Autowired
  private TripParticipantRepository tripParticipantRepository;

  @Qualifier("tripNotificationRepository")
  @Autowired
  private TripNotificationRepository tripNotificationRepository;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Qualifier("userNotificationRepository")
  @Autowired
  private UserNotificationRepository userNotificationRepository;

  private TripParticipant testParticipant1;

  private ParticipantConnection participantConnection;

  private List<ParticipantConnection> participantConnections;

  private TripParticipantService tripParticipantService;

  @Autowired
  private NotificationService notificationService;
  @Autowired
  private ConnectionService connectionService;

  private Trip testTrip1;

  private User testUser1;
  @BeforeEach
  void setup() {
    // Clear any existing data in the repositories
    tripRepository.deleteAll();
    tripRepository.flush();

    testUser1 = new User();
    testUser1.setId(1L);
    testUser1.setUsername("user1");
    testUser1.setPassword("Firstname Lastname");
    testUser1.setStatus(UserStatus.ONLINE);
    testUser1.setToken("abc");
    testUser1.setCreationDate(LocalDate.of(2020,11,11));
    testUser1.setBirthday(LocalDate.of(2020,11,11));
    testUser1.setEmail("firstname.lastname@something.com");
    testUser1.setLevel(1.00);
    testUser1.setLastOnline(LocalDateTime.of(2030,11,11,11,11));


    testUser1 = userRepository.save(testUser1);
    userRepository.flush();

    testTrip1 = new Trip();
    testTrip1.setTripName("Como");
    testTrip1.setTripDescription("We are going to Como this spring.");
    testTrip1.setCompleted(false);
    testTrip1.setMaxParticipants(10);
    testTrip1.setNumberOfParticipants(1);
    Station station = new Station();
    station.setStationCode("8301307");
    station.setStationName("Como S. Giovanni");
    testTrip1.setMeetUpPlace(station);
    testTrip1.setMeetUpTime(LocalDateTime.of(2000,11,11,11,11));
    testTrip1.setAdministrator(testUser1);

    testTrip1 = tripRepository.save(testTrip1);
    tripRepository.flush();

    testParticipant1 = new TripParticipant();
    testParticipant1.setTrip(testTrip1);
    testParticipant1.setStatus(InvitationStatus.ACCEPTED);
    testParticipant1.setInvitator(testUser1);
    testParticipant1.setUser(testUser1);

    testParticipant1 = tripParticipantRepository.save(testParticipant1);
    tripParticipantRepository.flush();

    participantConnection = new ParticipantConnection();
    participantConnection.setConnectionName("connection");
    participantConnection.setConnectionType(ConnectionType.BUS);
    participantConnection.setArrivalPoint(station);
    participantConnection.setArrivalTime(LocalDateTime.of(2024, 12, 12, 12, 0, 0));
    participantConnection.setDeparturePoint(station);
    participantConnection.setDepartureTime(LocalDateTime.of(2024, 12, 12, 12, 0, 0));
    participantConnection.setId(1L);
    participantConnections = new ArrayList<ParticipantConnection>();
    participantConnections.add(participantConnection);

  }
  @Test
  void getLocationsCoord_validInput_listOfStations() {
    Station station = ConnectionService.getLocationsCoord("47.476417", "8.307706");

    assertNotNull(station);
    assertEquals("Baden", station.getStationName());
  }
  @Test
  void getLocationsName_validInput_listOfStations() {
    List<Station> stations = ConnectionService.getLocationsName("Zurich");

    assertEquals("Zürich HB", stations.get(0).getStationName());
    assertEquals("8503000", stations.get(0).getStationCode());
  }

  @Test
  void getConnectionsByCode_validInput_listOfListOfConnections() {
    List<List<Connection>> connectionsList = ConnectionService.getConnectionsByCode("8503000", "8587010", "2024-12-12", "17:30", false);

    assertEquals("Zürich HB", connectionsList.get(0).get(0).getDeparturePoint().getStationName());
  }

  @Test
  void saveConnection_validInput_connectionSaved() {
    connectionService.saveConnection(testParticipant1, participantConnections);

    assertEquals(participantConnections.get(0).getConnectionName(), connectionService.getConnection(testParticipant1).get(0).getConnectionName());
  }

  @Test
  void updateConnection_validInput_connectionSaved() {
    connectionService.updateConnection(testParticipant1, participantConnections);

    assertEquals(participantConnections.get(0).getConnectionName(), connectionService.getConnection(testParticipant1).get(0).getConnectionName());
  }

  @Test
  void getDateString_validInput_success() {
    String dateString = connectionService.getDateString(LocalDateTime.of(2000, 1, 1, 12, 0));
    assertEquals("2000-01-01", dateString);
  }

  @Test
  void getTimeString_validInput_success() {
    String timeString = connectionService.getTimeString(LocalDateTime.of(2000, 1, 1, 12, 0));
    assertEquals("12:00", timeString);
  }
}
