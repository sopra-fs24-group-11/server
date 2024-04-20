package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class TripParticipantServiceIntegrationTest {

  @Qualifier("tripParticipantRepository")
  @Autowired
  private TripParticipantRepository tripParticipantRepository;

  @Qualifier("tripRepository")
  @Autowired
  private TripRepository tripRepository;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TripParticipantService tripParticipantService;

  @MockBean
  private ConnectionService connectionService;

  @MockBean
  private ListService listService;

  private User testUser1;
  private User testUser2;
  private Trip testTrip1;

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

    testUser1 = userRepository.save(testUser1);
    testUser2 = userRepository.save(testUser2);
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
  }

  @Test
  public void testStoreParticipants_success() {
    // Prepare arguments
    List<User> invited = new ArrayList<>();
    invited.add(testUser2);
    tripParticipantService.storeParticipants(testTrip1, testUser1, invited);

    // Verify that the trip participants have been created
    List<TripParticipant> participants = tripParticipantRepository.findAllByTrip(testTrip1);

    assertNotNull(participants);
    assertEquals(2, participants.size());
    assertEquals(testTrip1.getId(), participants.get(0).getTrip().getId());
    assertEquals(testUser1.getId(), participants.get(0).getUser().getId());
    assertEquals(InvitationStatus.ACCEPTED, participants.get(0).getStatus());
    assertEquals(testTrip1.getId(), participants.get(1).getTrip().getId());
    assertEquals(testUser2.getId(), participants.get(1).getUser().getId());
    assertEquals(InvitationStatus.PENDING, participants.get(1).getStatus());
    assertEquals(testUser1.getId(), participants.get(1).getInvitator().getId());
  }


}
