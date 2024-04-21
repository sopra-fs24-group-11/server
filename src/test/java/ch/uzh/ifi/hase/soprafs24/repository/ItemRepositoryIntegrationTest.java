package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private ItemRepository itemRepository;

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

  private Item createItemDummy(User user, Trip trip, TripParticipant participant, String thing) {
    Item item = new Item();
    item.setUserId(user.getId());
    item.setItem(thing);
    item.setCompleted(false);
    item.setParticipant(participant);
    item.setItemType(ItemType.INDIVIDUALPACKING);
    item.setTrip(trip);
    return item;
  }

  @Test
  void findAllByTripAndItemType_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    entityManager.persist(participant1);
    entityManager.flush();

    Item item = createItemDummy(user, trip, participant1, "Banana");
    entityManager.persist(item);
    entityManager.flush();

    // when
    List<Item> items = itemRepository.findAllByTripAndItemType(trip, ItemType.INDIVIDUALPACKING);

    // then
    assertNotNull(items);
    assertEquals(1, items.size());
    assertTrue(items.contains(item));
  }

  @Test
  void findAllByTripAndItemTypeAndParticipant_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    entityManager.persist(participant1);
    entityManager.flush();

    Item item = createItemDummy(user, trip, participant1, "Banana");
    entityManager.persist(item);
    entityManager.flush();

    // when
    List<Item> items = itemRepository.findAllByTripAndItemTypeAndParticipant(trip, ItemType.INDIVIDUALPACKING, participant1);

    // then
    assertNotNull(items);
    assertEquals(1, items.size());
    assertTrue(items.contains(item));
  }

  @Test
  void findAllByUserId_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    entityManager.persist(participant1);
    entityManager.flush();

    Item item = createItemDummy(user, trip, participant1, "Banana");
    entityManager.persist(item);
    entityManager.flush();

    // when
    List<Item> items = itemRepository.findAllByUserId(user.getId());

    // then
    assertNotNull(items);
    assertEquals(1, items.size());
    assertTrue(items.contains(item));
  }

  @Test
  void findAllByParticipant_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    entityManager.persist(participant1);
    entityManager.flush();

    Item item = createItemDummy(user, trip, participant1, "Banana");
    entityManager.persist(item);
    entityManager.flush();

    // when
    List<Item> items = itemRepository.findAllByParticipant(participant1);

    // then
    assertNotNull(items);
    assertEquals(1, items.size());
    assertTrue(items.contains(item));
  }

  @Test
  void deleteAllByParticipantAndItemType_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    entityManager.persist(participant1);
    entityManager.flush();

    Item item = createItemDummy(user, trip, participant1, "Banana");
    entityManager.persist(item);
    entityManager.flush();

    // when
    itemRepository.deleteAllByParticipantAndItemType(participant1, ItemType.INDIVIDUALPACKING);

    // then
    List<Item> items = itemRepository.findAllByParticipant(participant1);
    assertEquals(0, items.size());
  }

  @Test
  void deleteAllByUserIdAndItemType_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    entityManager.persist(participant1);
    entityManager.flush();

    Item item = createItemDummy(user, trip, participant1, "Banana");
    entityManager.persist(item);
    entityManager.flush();

    // when
    itemRepository.deleteAllByUserIdAndItemType(user.getId(), ItemType.INDIVIDUALPACKING);

    // then
    List<Item> items = itemRepository.findAllByUserId(user.getId());
    assertEquals(0, items.size());
  }

  @Test
  void deleteAllByTrip_success() {
    // given
    User user = createUserDummy("user1", "abc");
    entityManager.persist(user);
    entityManager.flush();

    Trip trip = createTripDummy(user);
    entityManager.persist(trip);
    entityManager.flush();

    TripParticipant participant1 = createTripParticipantDummy(user, trip);
    entityManager.persist(participant1);
    entityManager.flush();

    Item item = createItemDummy(user, trip, participant1, "Banana");
    entityManager.persist(item);
    entityManager.flush();

    // when
    itemRepository.deleteAllByTrip(trip);

    // then
    List<Item> items = itemRepository.findAllByTripAndItemType(trip, ItemType.INDIVIDUALPACKING);
    assertEquals(0, items.size());
  }

}
