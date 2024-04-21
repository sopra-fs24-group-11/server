package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripNotification;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TripNotificationRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TripNotificationRepository tripNotificationRepository;

  private User createUserDummy() {
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.ONLINE);
    user.setToken("abc123");
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

  @Test
  void findAllByTrip_success() {
    // given
    User user =  createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripNotification tn1 = new TripNotification();
    tn1.setMessage("This is the first message");
    tn1.setTimeStamp(LocalDateTime.of(2024, 11, 11, 11, 11));
    tn1.setTrip(trip);

    TripNotification tn2 = new TripNotification();
    tn2.setMessage("This is the second message");
    tn2.setTimeStamp(LocalDateTime.of(2024, 11, 12, 11, 11));
    tn2.setTrip(trip);

    entityManager.persist(tn1);
    entityManager.persist(tn2);
    entityManager.flush();

    // when
    List<TripNotification> found = tripNotificationRepository.findAllByTrip(trip);

    // then
    assertEquals(2, found.size());
    assertTrue(found.contains(tn1));
    assertTrue(found.contains(tn2));
  }

  @Test
  void deleteAllByTrip_success() {
    // given
    User user =  createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripNotification tn1 = new TripNotification();
    tn1.setMessage("This is the first message");
    tn1.setTimeStamp(LocalDateTime.of(2024, 11, 11, 11, 11));
    tn1.setTrip(trip);

    TripNotification tn2 = new TripNotification();
    tn2.setMessage("This is the second message");
    tn2.setTimeStamp(LocalDateTime.of(2024, 11, 12, 11, 11));
    tn2.setTrip(trip);

    entityManager.persist(tn1);
    entityManager.persist(tn2);
    entityManager.flush();

    // when
    tripNotificationRepository.deleteAllByTrip(trip);

    // then
    List<TripNotification> found = tripNotificationRepository.findAllByTrip(trip);
    assertEquals(0, found.size());
  }

}
