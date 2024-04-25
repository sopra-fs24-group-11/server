package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebAppConfiguration
@SpringBootTest
@Transactional
@Rollback
public class ListServiceIntegrationTest {
  @Qualifier("participantConnectionRepository")
  @Autowired
  private ParticipantConnectionRepository participantConnectionRepository;

  @Qualifier("tripRepository")
  @Autowired
  private TripRepository tripRepository;

  @Qualifier("itemRepository")
  @Autowired
  private ItemRepository itemRepository;

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


  private ParticipantConnection participantConnection;

  private List<ParticipantConnection> participantConnections;

  private TripParticipantService tripParticipantService;

  @Autowired
  private NotificationService notificationService;
  @Autowired
  private ConnectionService connectionService;
  @Autowired
  private ListService listService;

  private Trip testTrip1;

  private User testUser1;

  private Item testItem1;
  private TripParticipant testParticipant1;

  private TripParticipant testParticipant2;
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

    testParticipant2 = new TripParticipant();
    testParticipant2.setTrip(testTrip1);
    testParticipant2.setStatus(InvitationStatus.PENDING);
    testParticipant2.setInvitator(testUser1);
    testParticipant2.setUser(testUser1);

    testParticipant1 = tripParticipantRepository.save(testParticipant1);
    testParticipant2 = tripParticipantRepository.save(testParticipant2);
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

    itemRepository.deleteAll();
    itemRepository.flush();

    testItem1 = new Item();
    testItem1.setUserId(1L);
    testItem1.setParticipant(testParticipant1);
    testItem1.setItemType(ItemType.GROUPPACKING);
    testItem1.setItem("this is a test item");
    testItem1.setTrip(testTrip1);
    testItem1.setCompleted(false);
    testItem1.setId(1L);
    testItem1 = itemRepository.save(testItem1);
  }

  @Test
  void getItems_validInput_listOfItems() {
    List<Item> items = listService.getItems(testTrip1, ItemType.GROUPPACKING, testParticipant1);

    assertEquals("this is a test item", items.get(0).getItem());
  }

  @Test
  void updateItem_validInput_success() {
    Item testItem2 = new Item();
    testItem2.setUserId(1L);
    testItem2.setParticipant(testParticipant1);
    testItem2.setItemType(ItemType.GROUPPACKING);
    testItem2.setItem("this is a changed test item");
    testItem2.setTrip(testTrip1);
    testItem2.setCompleted(false);
    testItem2.setId(1L);
    listService.updateItem(1L, testItem2);

    assertEquals("this is a changed test item", itemRepository.findById(1L).get().getItem());
  }

  /*@Test
  void updateResponsible_validInput_success() {
    listService.updateResponsible(1L, testParticipant2);
    assertEquals(testParticipant2, itemRepository.findById(1L).get().getParticipant());
  }*/

  void addItem_validInput_success() {
    Item testItem2 = new Item();
    testItem2.setUserId(1L);
    testItem2.setParticipant(testParticipant1);
    testItem2.setItem("this is a second test item");
    testItem2.setId(2L);
    listService.addItem(testTrip1, testItem2, ItemType.GROUPPACKING, testParticipant1);
    assertEquals("this is a second test item", itemRepository.findById(2L).get().getItem());
  }
}
