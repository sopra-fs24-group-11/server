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
@Transactional
@Rollback
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


  private User testUser1;
  private User testUser2;
  private Trip testTrip1;
  private Trip testTrip2;
  private TripParticipant testParticipant1;
  private TripParticipant testParticipant2;

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

    testTrip2 = new Trip();
    testTrip2.setTripName("Como");
    testTrip2.setTripDescription("We are going to Como this spring.");
    testTrip2.setCompleted(false);
    testTrip2.setMaxParticipants(10);
    testTrip2.setNumberOfParticipants(1);
    testTrip2.setMeetUpPlace(station);
    testTrip2.setMeetUpTime(LocalDateTime.of(2000,11,11,11,11));
    testTrip2.setAdministrator(testUser1);

    testTrip1 = tripRepository.save(testTrip1);
    testTrip2 = tripRepository.save(testTrip2);
    tripRepository.flush();

    testParticipant1 = new TripParticipant();
    testParticipant1.setTrip(testTrip2);
    testParticipant1.setStatus(InvitationStatus.ACCEPTED);
    testParticipant1.setInvitator(testUser1);
    testParticipant1.setUser(testUser1);

    testParticipant2 = new TripParticipant();
    testParticipant2.setTrip(testTrip2);
    testParticipant2.setStatus(InvitationStatus.PENDING);
    testParticipant2.setInvitator(testUser1);
    testParticipant2.setUser(testUser2);

    testParticipant1 = tripParticipantRepository.save(testParticipant1);
    testParticipant2 = tripParticipantRepository.save(testParticipant2);
    tripParticipantRepository.flush();
  }

  @Test
  public void testStoreParticipants_success() {
    // Prepare invited users
    List<User> invited = new ArrayList<>();
    invited.add(testUser2);
    tripParticipantService.storeParticipants(testTrip1, testUser1, invited);

    // Verify that the trip participants have been created
    List<TripParticipant> participants = tripParticipantRepository.findAllByTrip(testTrip1);

    assertNotNull(participants);
    assertEquals(2, participants.size());
    assertEquals(testTrip1.getId(), participants.get(0).getTrip().getId());
    assertEquals(testTrip1.getId(), participants.get(1).getTrip().getId());
  }

  @Test
  public void testStoreParticipant_success() {
    tripParticipantService.storeParticipant(testTrip1, testUser1, testUser2);

    // Verify that the trip participant has been created
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(testUser2, testTrip1);

    assertNotNull(participant);
    assertEquals(testTrip1.getId(),participant.getTrip().getId());
    assertEquals(testUser2.getId(),participant.getUser().getId());
  }

  @Test
  public void testDeleteAllForAUser_success() {
    tripParticipantService.deleteAllForAUser(testUser2);

    // Verify that all trip participants of the user have been deleted
    List<TripParticipant> participants = tripParticipantRepository.findAllByUser(testUser2);

    assertEquals(0, participants.size());
  }

  @Test
  public void testDeleteAllForAUser_isAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.deleteAllForAUser(testUser1));
  }
  @Test
  public void testIsPartOfTripAndHasAccepted_success() {
    assertDoesNotThrow(() -> tripParticipantService.isPartOfTripAndHasAccepted(testUser1, testTrip2));
  }
  @Test
  public void testIsPartOfTripAndHasAccepted_notAccepted_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.isPartOfTripAndHasAccepted(testUser2, testTrip2));
  }
  @Test
  public void testMarkTripAsFavorite_success() {
    // when
    tripParticipantService.markTripAsFavorite(testUser1, testTrip2);
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(testUser1, testTrip2);

    // then
    assertTrue(participant.isFavouriteTrip());
  }
  @Test
  public void testMarkTripAsFavorite_notParticipant_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.markTripAsFavorite(testUser2, testTrip1));
  }

  @Test
  public void testAcceptInvitation_success() {
    // when
    tripParticipantService.acceptInvitation(testUser2, testTrip2);
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(testUser1, testTrip2);

    // then
    assertEquals(InvitationStatus.ACCEPTED, participant.getStatus());
  }
  @Test
  public void testAcceptInvitation_notParticipant_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.acceptInvitation(testUser2, testTrip1));
  }

  @Test
  public void testRejectInvitation_success() {
    // when
    tripParticipantService.rejectInvitation(testUser2, testTrip2);
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(testUser2, testTrip2);

    // then
    assertNull(participant);
  }
  @Test
  public void testRejectInvitation_notParticipant_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.rejectInvitation(testUser2, testTrip1));
  }
  @Test
  public void testRejectInvitation_isAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.rejectInvitation(testUser1, testTrip2));
  }
  @Test
  public void testRejectInvitation_alreadyAccepted_throwsError() {
    tripParticipantService.acceptInvitation(testUser2, testTrip2);
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.rejectInvitation(testUser2, testTrip2));
  }
  @Test
  public void testLeaveTrip_success() {
    // when
    tripParticipantService.acceptInvitation(testUser2, testTrip2);
    tripParticipantService.leaveTrip(testUser2, testTrip2);
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(testUser2, testTrip2);

    // then
    assertNull(participant);
  }
  @Test
  public void testLeaveTrip_notParticipant_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.leaveTrip(testUser2, testTrip1));
  }
  @Test
  public void testLeaveTrip_isAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.leaveTrip(testUser1, testTrip2));
  }
  @Test
  public void testLeaveTrip_notYetAccepted_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.leaveTrip(testUser2, testTrip2));
  }

  @Test
  public void testRemoveMemberFromTrip_success() {
    // when
    tripParticipantService.removeMemberFromTrip(testUser2, testUser1, testTrip2);

    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(testUser2, testTrip2);

    // then
    assertNull(participant);
  }
  @Test
  public void testRemoveMemberFromTrip_notParticipant_throwsError() {
    // when
    tripParticipantService.rejectInvitation(testUser2, testTrip2);
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.removeMemberFromTrip(testUser2, testUser1, testTrip2));
  }
  @Test
  public void testRemoveMemberFromTrip_notAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.removeMemberFromTrip(testUser1, testUser2, testTrip2));
  }
  @Test
  public void testRemoveMemberFromTrip_isAdmin_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.removeMemberFromTrip(testUser1, testUser1, testTrip2));
  }
  @Test
  public void testDeleteEverythingRelatedToATrip_success() {
    // when
    tripParticipantService.deleteEverythingRelatedToATrip(testTrip2);

    List<TripParticipant> participants = tripParticipantRepository.findAllByTrip(testTrip2);

    // then
    assertEquals(0, participants.size());
  }


  @Test
  public void testGetTripParticipant_success() {
    // when
    TripParticipant participant = tripParticipantService.getTripParticipant(testTrip2, testUser1);

    // then
    assertEquals(testUser1.getId(), participant.getUser().getId());
  }
  @Test
  public void testGetTripParticipant_notParticipant_throwsError() {
    assertThrows(ResponseStatusException.class, () -> tripParticipantService.getTripParticipant(testTrip1, testUser1));
  }

  @Test
  public void testGetTripUsers_success() {
    // when
    List<User> users = tripParticipantService.getTripUsers(testTrip2);

    // then
    assertNotNull(users);
    assertEquals(2, users.size());
  }
  @Test
  public void testGetTripUsersWhoHaveAccepted_success() {
    // when
    List<User> users = tripParticipantService.getTripUsersWhoHaveAccepted(testTrip2);

    // then
    assertNotNull(users);
    assertEquals(1, users.size());
  }
  @Test
  public void testGetTripUsersWithoutAdmin_success() {
    // when
    List<User> users = tripParticipantService.getTripUsersWithoutAdmin(testTrip2);

    // then
    assertNotNull(users);
    assertEquals(1, users.size());
  }

}
