package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class TripParticipantRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TripParticipantRepository tripParticipantRepository;

  private User createUserDummy(String un, String to) {
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername(un);
    user.setStatus(UserStatus.ONLINE);
    user.setToken(to);
    user.setCreationDate(LocalDate.of(2020,11,11));
    user.setBirthday(LocalDate.of(2020,11,11));
    user.setEmail("firstname.lastname@something.com");
    user.setLevel(1.00);
    user.setLastOnline(LocalDateTime.of(2030,11,11,11,11));
    return user;
  }

  private Trip createTripDummy(User user) {
    Trip trip = new Trip();
    trip.setTripName("Como");
    trip.setTripDescription("We are going to Como this spring.");
    trip.setCompleted(false);
    trip.setMaxParticipants(10);
    trip.setNumberOfParticipants(1);
    Station station = new Station();
    station.setStationCode("8301307");
    station.setStationName("Como S. Giovanni");
    trip.setMeetUpPlace(station);
    trip.setMeetUpTime(LocalDateTime.of(2000,11,11,11,11)); // in the past
    trip.setAdministrator(user);
    return trip;
  }

  private TripParticipant createTripParticipantDummy(User user, Trip trip) {
    TripParticipant participant = new TripParticipant();
    participant.setUser(user);
    participant.setTrip(trip);
    participant.setInvitator(user);
    participant.setFavouriteTrip(false);
    participant.setStatus(InvitationStatus.ACCEPTED);
    return participant;
  }

  @Test
  public void findAllByTrip_success() {
    // given
    User user1 = createUserDummy("user1", "abc");
    User user2 = createUserDummy("user2", "123");
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.flush();

    Trip trip = createTripDummy(user1);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user1, trip);
    TripParticipant participant2 = createTripParticipantDummy(user2, trip);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    List<TripParticipant> participants = tripParticipantRepository.findAllByTrip(trip);

    // then
    assertNotNull(participants);
    assertEquals(participants.size(), 2);
    assertTrue(participants.contains(participant1));
    assertTrue(participants.contains(participant2));
  }

  @Test
  public void findAllByUserAndFavouriteTrip_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip1 = createTripDummy(user);
    Trip trip2 = createTripDummy(user);
    entityManager.persist(trip1);
    entityManager.persist(trip2);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip1);
    TripParticipant participant2 = createTripParticipantDummy(user, trip2);
    participant1.setFavouriteTrip(true);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndFavouriteTrip(user, true);

    // then
    assertNotNull(participants);
    assertEquals(participants.size(), 1);
    assertTrue(participants.contains(participant1));
  }
  @Test
  public void findAllByUserAndTripCompletedAndStatus_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    TripParticipant participant2 = createTripParticipantDummy(user, trip);
    participant1.setStatus(InvitationStatus.ACCEPTED);
    participant2.setStatus(InvitationStatus.PENDING);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompletedAndStatus(user, false, InvitationStatus.ACCEPTED);

    // then
    assertNotNull(participants);
    assertEquals(participants.size(), 1);
    assertTrue(participants.contains(participant1));
  }

  @Test
  public void findByUserAndTripAndStatus_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    TripParticipant participant2 = createTripParticipantDummy(user, trip);
    participant1.setStatus(InvitationStatus.ACCEPTED);
    participant2.setStatus(InvitationStatus.PENDING);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    TripParticipant foundParticipant = tripParticipantRepository.findByUserAndTripAndStatus(user, trip, InvitationStatus.ACCEPTED);

    // then
    assertNotNull(foundParticipant);
    assertEquals(foundParticipant, participant1);
  }
  @Test
  public void findAllByUserAndTripCompleted_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip1 = createTripDummy(user);
    Trip trip2 = createTripDummy(user);
    entityManager.persist(trip1);
    entityManager.persist(trip2);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip1);
    TripParticipant participant2 = createTripParticipantDummy(user, trip2);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompleted(user, false);

    // then
    assertNotNull(participants);
    assertEquals(participants.size(), 2);
    assertTrue(participants.contains(participant1));
    assertTrue(participants.contains(participant2));
  }

  @Test
  public void findAllByUserAndTripAdministrator_success() {
    // given
    User user1 = createUserDummy("user1", "abc");
    User user2 = createUserDummy("user2", "123");
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.flush();

    Trip trip1 = createTripDummy(user1);
    Trip trip2 = createTripDummy(user2);
    entityManager.persist(trip1);
    entityManager.persist(trip2);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user1, trip1);
    TripParticipant participant2 = createTripParticipantDummy(user2, trip2);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripAdministrator(user1, user1);

    // then
    assertNotNull(participants);
    assertEquals(participants.size(), 1);
    assertTrue(participants.contains(participant1));
  }

  @Test
  public void findByUserAndTrip_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip1 = createTripDummy(user);
    Trip trip2 = createTripDummy(user);
    entityManager.persist(trip1);
    entityManager.persist(trip2);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip1);
    TripParticipant participant2 = createTripParticipantDummy(user, trip2);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    TripParticipant foundParticipant = tripParticipantRepository.findByUserAndTrip(user, trip1);

    // then
    assertNotNull(foundParticipant);
    assertEquals(foundParticipant, participant1);
  }

  @Test
  public void findAllByUser_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip1 = createTripDummy(user);
    Trip trip2 = createTripDummy(user);
    entityManager.persist(trip1);
    entityManager.persist(trip2);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip1);
    TripParticipant participant2 = createTripParticipantDummy(user, trip2);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    List<TripParticipant> participants = tripParticipantRepository.findAllByUser(user);

    // then
    assertNotNull(participants);
    assertEquals(participants.size(), 2);
    assertTrue(participants.contains(participant1));
    assertTrue(participants.contains(participant2));
  }

  @Test
  public void findAllByTripAndStatus_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    TripParticipant participant2 = createTripParticipantDummy(user, trip);
    participant1.setStatus(InvitationStatus.ACCEPTED);
    participant2.setStatus(InvitationStatus.PENDING);
    entityManager.persist(participant1);
    entityManager.persist(participant2);
    entityManager.flush();

    // when
    List<TripParticipant> participants = tripParticipantRepository.findAllByTripAndStatus(trip, InvitationStatus.ACCEPTED);

    // then
    assertNotNull(participants);
    assertEquals(participants.size(), 1);
    assertTrue(participants.contains(participant1));
  }
}
