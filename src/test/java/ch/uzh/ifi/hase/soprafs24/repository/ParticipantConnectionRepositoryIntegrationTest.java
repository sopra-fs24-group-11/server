package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import org.apache.tomcat.jni.Local;
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
public class ParticipantConnectionRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ParticipantConnectionRepository participantConnectionRepository;

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

  public ParticipantConnection createParticipantConnectionDummy(TripParticipant participant) {
    ParticipantConnection connection = new ParticipantConnection();
    connection.setParticipant(participant);
    connection.setConnectionName("B 37");
    connection.setConnectionType(ConnectionType.BUS);
    Station departurePoint = new Station();
    departurePoint.setStationName("Zürich, Aspholz");
    departurePoint.setStationCode("8591046");
    Station arrivalPoint = new Station();
    arrivalPoint.setStationName("Zürich, ETH Hönggerberg");
    arrivalPoint.setStationCode("8591122");
    connection.setDeparturePoint(departurePoint);
    connection.setArrivalPoint(arrivalPoint);
    connection.setDepartureTime(LocalDateTime.of(2024,11,11,11,15));
    connection.setArrivalTime(LocalDateTime.of(2024,11,11,11,25));
    return connection;
  }

  @Test
  public void findAllByParticipant_success() {
    // given
    User user =  createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant = createTripParticipantDummy(user, trip);
    entityManager.persist(participant);
    entityManager.flush();

    ParticipantConnection connection1 = createParticipantConnectionDummy(participant);
    ParticipantConnection connection2 = createParticipantConnectionDummy(participant);

    entityManager.persist(connection1);
    entityManager.persist(connection2);
    entityManager.flush();

    // when
    List<ParticipantConnection> connections = participantConnectionRepository.findAllByParticipant(participant);

    // then
    assertEquals(connections.size(), 2);
    assertTrue(connections.contains(connection1));
    assertTrue(connections.contains(connection2));
    assertEquals(connections.get(0).getParticipant(), participant);
    assertEquals(connections.get(1).getParticipant(), participant);
  }

  @Test
  public void deleteAllByParticipant_success() {
    // given
    User user = createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant = createTripParticipantDummy(user, trip);
    entityManager.persist(participant);
    entityManager.flush();

    ParticipantConnection connection1 = createParticipantConnectionDummy(participant);
    ParticipantConnection connection2 = createParticipantConnectionDummy(participant);

    entityManager.persist(connection1);
    entityManager.persist(connection2);
    entityManager.flush();

    // when
    participantConnectionRepository.deleteAllByParticipant(participant);

    // then
    List<ParticipantConnection> connections = participantConnectionRepository.findAllByParticipant(participant);
    assertEquals(connections.size(), 0);
  }

}
