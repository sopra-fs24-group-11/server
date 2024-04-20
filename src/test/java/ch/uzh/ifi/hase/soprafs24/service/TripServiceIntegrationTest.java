package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.FriendshipRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TripParticipantRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TripRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
@Transactional
@Rollback
public class TripServiceIntegrationTest {

  @Qualifier("tripParticipantRepository")
  @Autowired
  private TripParticipantRepository tripParticipantRepository;

  @Qualifier("tripRepository")
  @Autowired
  private TripRepository tripRepository;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Qualifier("friendshipRepository")
  @Autowired
  private FriendshipRepository friendshipRepository;

  @Autowired
  private TripService tripService;



  private User testUser1;
  private User testUser2;
  private User testUser3;
  private Trip testTrip1;
  private Trip testTrip2;
  private Trip testTrip3;
  private Friendship testFriendship;

  @BeforeEach
  public void setup() {
    // Clear any existing data in the repositories
    userRepository.deleteAll();
    userRepository.flush();
    tripRepository.deleteAll();
    tripRepository.flush();

    // Create test user
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

    testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setUsername("user2");
    testUser2.setPassword("Firstname Lastname");
    testUser2.setStatus(UserStatus.ONLINE);
    testUser2.setToken("123");
    testUser2.setCreationDate(LocalDate.of(2020,11,11));
    testUser2.setBirthday(LocalDate.of(2020,11,11));
    testUser2.setEmail("firstname.lastname@something.com");
    testUser2.setLevel(1.00);
    testUser2.setLastOnline(LocalDateTime.of(2030,11,11,11,11));

    testUser3 = new User();
    testUser3.setId(3L);
    testUser3.setUsername("user3");
    testUser3.setPassword("Firstname Lastname");
    testUser3.setStatus(UserStatus.ONLINE);
    testUser3.setToken("xyz");
    testUser3.setCreationDate(LocalDate.of(2020,11,11));
    testUser3.setBirthday(LocalDate.of(2020,11,11));
    testUser3.setEmail("firstname.lastname@something.com");
    testUser3.setLevel(1.00);
    testUser3.setLastOnline(LocalDateTime.of(2030,11,11,11,11));

    testUser1 = userRepository.save(testUser1);
    testUser2 = userRepository.save(testUser2);
    testUser3 = userRepository.save(testUser3);
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

    testTrip2 = new Trip();
    testTrip2.setTripName("Como");
    testTrip2.setTripDescription("We are going to Como this spring.");
    testTrip2.setCompleted(false);
    testTrip2.setMaxParticipants(10);
    testTrip2.setNumberOfParticipants(1);
    testTrip2.setMeetUpPlace(station);
    testTrip2.setMeetUpTime(LocalDateTime.of(2000,11,11,11,11));
    testTrip2.setAdministrator(testUser1);

    testTrip3 = new Trip();
    testTrip3.setTripName("New");
    testTrip3.setTripDescription("We are going to New this spring.");
    testTrip3.setCompleted(false);
    testTrip3.setMaxParticipants(10);
    testTrip3.setNumberOfParticipants(1);
    testTrip3.setMeetUpPlace(station);
    testTrip3.setMeetUpTime(LocalDateTime.of(2000,11,11,11,11));
    testTrip3.setAdministrator(testUser1);

    testTrip1 = tripRepository.save(testTrip1);
    testTrip2 = tripRepository.save(testTrip2);
    tripRepository.flush();

    testFriendship = new Friendship();
    testFriendship.setFriend1(testUser1);
    testFriendship.setFriend2(testUser2);
    testFriendship.setStatus(FriendShipStatus.ACCEPTED);
    testFriendship.setPoints(0);

    testFriendship = friendshipRepository.save(testFriendship);
    friendshipRepository.flush();
  }

  @Test
  public void testGetTripById_success() {
    Trip trip = tripService.getTripById(testTrip1.getId());

    assertEquals(testTrip1.getId(), trip.getId());
  }

  @Test
  public void testGetTripById_notExists_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.getTripById(102031321L));
  }

  @Test
  public void testCreateTrip_success() {
    Long id = tripService.createTrip(testTrip3, testUser1, asList(testUser2.getId()));

    Trip trip = tripService.getTripById(id);

    assertEquals(testTrip3.getTripName(), trip.getTripName());
    assertEquals(testTrip3.getMeetUpTime(), trip.getMeetUpTime());
    assertEquals(testTrip3.getTripDescription(), trip.getTripDescription());
  }
  @Test
  public void testCreateTrip_inviteYourself_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.createTrip(testTrip3, testUser1, asList(testUser1.getId())));
  }
  @Test
  public void testCreateTrip_notAFriend_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.createTrip(testTrip3, testUser1, asList(testUser3.getId())));
  }

  @Test
  public void testUpdateTrip_success() {
    TripParticipant testParticipant1 = new TripParticipant();
    testParticipant1.setTrip(testTrip2);
    testParticipant1.setStatus(InvitationStatus.ACCEPTED);
    testParticipant1.setInvitator(testUser1);
    testParticipant1.setUser(testUser1);
    tripParticipantRepository.save(testParticipant1);
    tripParticipantRepository.flush();
    tripService.updateTrip(testTrip2, testTrip3, testUser1, asList(testUser2.getId()));

    Trip trip = tripService.getTripById(testTrip2.getId());

    assertEquals(testTrip3.getTripName(), trip.getTripName());
    assertEquals(testTrip3.getMeetUpTime(), trip.getMeetUpTime());
    assertEquals(testTrip3.getTripDescription(), trip.getTripDescription());
  }
  @Test
  public void testUpdateTrip_inviteYourself_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.updateTrip(testTrip2, testTrip3, testUser1, asList(testUser1.getId())));
  }
  @Test
  public void testUpdateTrip_notAFriend_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.updateTrip(testTrip2, testTrip3, testUser1, asList(testUser3.getId())));
  }
  @Test
  public void testUpdateTrip_notAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.updateTrip(testTrip2, testTrip3, testUser2, asList(testUser1.getId())));
  }
  @Test
  public void testIsAdmin_success() {
    assertTrue(tripService.isAdmin(testTrip1, testUser1));
  }
  @Test
  public void testNewAdmin_success() {
    Long id = tripService.createTrip(testTrip3, testUser1, asList(testUser2.getId()));
    Trip trip = tripService.getTripById(id);
    tripService.newAdmin(trip, testUser1, testUser1);
    trip = tripService.getTripById(id);

    assertTrue(tripService.isAdmin(testTrip1, testUser1));
    assertEquals(testUser1.getId(), trip.getAdministrator().getId());
  }
  @Test
  public void testNewAdmin_notAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.newAdmin(testTrip1, testUser2, testUser2));
  }
  @Test
  public void testNewAdmin_notYetAccepted_throwsError() {
    Long id = tripService.createTrip(testTrip3, testUser1, asList(testUser2.getId()));
    Trip trip = tripService.getTripById(id);
    assertThrows(ResponseStatusException.class, () -> tripService.newAdmin(trip, testUser1, testUser2));
  }
  @Test
  public void testIsOngoing_success() {
    testTrip1.setCompleted(false);
    assertDoesNotThrow(() -> tripService.isOngoing(testTrip1));
  }
  @Test
  public void testIsOngoing_tripCompleted_throwsError() {
    testTrip1.setCompleted(true);
    assertThrows(ResponseStatusException.class, () -> tripService.isOngoing(testTrip1));
  }
  @Test
  public void testDeleteTrip_notAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripService.deleteTrip(testTrip1, testUser2));
  }
  @Test
  public void testDeleteTrip_success() {
    Long id = tripService.createTrip(testTrip3, testUser1, asList(testUser2.getId()));
    Trip trip = tripService.getTripById(id);
    tripService.deleteTrip(trip, testUser1);

    assertThrows(ResponseStatusException.class, () -> tripService.getTripById(testTrip1.getId()));

  }
  @Test
  public void testMarkTripsAsCompleted_success() {
    Long id = tripService.createTrip(testTrip3, testUser1, asList(testUser2.getId()));
    tripService.markTripsAsCompleted();

    List<Trip> ongoingTrips = tripRepository.findAllByCompletedFalseAndMeetUpTimeBefore(LocalDateTime.of(2020,11,11,11,11));

    assertEquals(0, ongoingTrips.size());

  }
}
