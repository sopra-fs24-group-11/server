package ch.uzh.ifi.hase.soprafs24.rest.mapper;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * DTOMapperTest
 * Tests if the mapping between the internal and the external/API representation
 * works.
 */
class DTOMapperTest {
  @Test
  void testConvertUserPostDTOtoEntity() {
    // create UserPostDTO
    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("1234");
    userPostDTO.setUsername("username");

    // MAP -> Create user
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // check content
    assertEquals(userPostDTO.getPassword(), user.getPassword());
    assertEquals(userPostDTO.getUsername(), user.getUsername());
  }

  @Test
  void testConvertEntityToUserGetDTO() {

    // create User
    User user = new User();
    user.setPassword("password123");
    user.setUsername("testUser");
    user.setStatus(UserStatus.ONLINE);
    user.setBirthday(LocalDate.of(2000, 11, 11));
    user.setEmail("test@example.com");
    user.setCreationDate(LocalDate.now());
    user.setLevel(1.0);

    // MAP -> Create UserGetDTO
    UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);

    // check content
    assertEquals(user.getId(), userGetDTO.getId());
    assertEquals(user.getPassword(), userGetDTO.getPassword());
    assertEquals(user.getUsername(), userGetDTO.getUsername());
    assertEquals(user.getStatus(), userGetDTO.getStatus());
    assertEquals(user.getBirthday(), userGetDTO.getBirthday());
    assertEquals(user.getEmail(), userGetDTO.getEmail());
    assertEquals(user.getCreationDate(), userGetDTO.getCreationDate());
    assertEquals(user.getLevel(), userGetDTO.getLevel());
  }

  @Test
  void testConvertUserLoginPostDTOtoEntity() {
    // Create UserLoginPostDTO
    UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
    userLoginPostDTO.setPassword("password123");
    userLoginPostDTO.setUsername("testUser");

    // MAP -> Create User
    User user = DTOMapper.INSTANCE.convertUserLoginPostDTOtoEntity(userLoginPostDTO);

    // Check content
    assertEquals(userLoginPostDTO.getPassword(), user.getPassword());
    assertEquals(userLoginPostDTO.getUsername(), user.getUsername());
  }

  @Test
  void testConvertUserPutDTOToEntity() {
    // Create UserPutDTO
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setPassword("password123");
    userPutDTO.setUsername("testUser");
    userPutDTO.setEmail("test@example.com");
    userPutDTO.setBirthday(LocalDate.of(2000,11,11));

    // MAP -> Create User
    User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);

    // Check content
    assertEquals(userPutDTO.getPassword(), user.getPassword());
    assertEquals(userPutDTO.getUsername(), user.getUsername());
    assertEquals(userPutDTO.getEmail(), user.getEmail());
    assertEquals(userPutDTO.getBirthday(), user.getBirthday());
  }

  @Test
  void testConvertEntityToMatchingUserGetDTO() {
    // Create User
    User user = new User();
    user.setId(1L);
    user.setUsername("testUser");
    user.setLevel(5.0);

    // MAP -> Create MatchingUserGetDTO
    MatchingUserGetDTO matchingUserGetDTO = DTOMapper.INSTANCE.convertEntityToMatchingUserGetDTO(user);

    // Check content
    assertEquals(user.getId(), matchingUserGetDTO.getId());
    assertEquals(user.getUsername(), matchingUserGetDTO.getUsername());
    assertEquals(user.getLevel(), matchingUserGetDTO.getLevel());
  }

  @Test
  void testConvertEntityToFriendGetDTO() {
    // Create Friend
    Friend friend = new Friend();
    friend.setFriendId(2L);
    friend.setUsername("friendUser");
    friend.setLevel(3.0);
    friend.setPoints(100);

    // MAP -> Create FriendGetDTO
    FriendGetDTO friendGetDTO = DTOMapper.INSTANCE.convertEntityToFriendGetDTO(friend);

    // Check content
    assertEquals(friend.getFriendId(), friendGetDTO.getFriendId());
    assertEquals(friend.getUsername(), friendGetDTO.getUsername());
    assertEquals(friend.getLevel(), friendGetDTO.getLevel());
    assertEquals(friend.getPoints(), friendGetDTO.getPoints());
  }

  @Test
  void testConvertTripPostDTOtoEntity() {
    // Create TripPostDTO
    TripPostDTO tripPostDTO = new TripPostDTO();
    tripPostDTO.setTripName("Test Trip");
    tripPostDTO.setTripDescription("This is a test trip.");
    tripPostDTO.setMeetUpTime(LocalDateTime.of(2024, 4, 20, 10, 0));
    Station station = new Station();
    station.setStationCode("8591122");
    station.setStationName("Zürich, ETH Hönggerberg");
    tripPostDTO.setMeetUpPlace(station);

    // MAP -> Create Trip
    Trip trip = DTOMapper.INSTANCE.convertTripPostDTOtoEntity(tripPostDTO);

    // Check content
    assertEquals(tripPostDTO.getTripName(), trip.getTripName());
    assertEquals(tripPostDTO.getTripDescription(), trip.getTripDescription());
    assertEquals(tripPostDTO.getMeetUpTime(), trip.getMeetUpTime());
    assertEquals(tripPostDTO.getMeetUpPlace(), trip.getMeetUpPlace());
  }

  @Test
  void testConvertTripPutDTOtoEntity() {
    // Create TripPutDTO
    TripPutDTO tripPutDTO = new TripPutDTO();
    tripPutDTO.setTripName("Updated Trip");
    tripPutDTO.setTripDescription("Updated trip description.");
    tripPutDTO.setMeetUpTime(LocalDateTime.of(2024, 5, 1, 12, 0));
    Station station = new Station();
    station.setStationCode("8591122");
    station.setStationName("Zürich, ETH Hönggerberg");
    tripPutDTO.setMeetUpPlace(station);
    tripPutDTO.setRating(4);

    // MAP -> Create Trip
    Trip trip = DTOMapper.INSTANCE.convertTripPutDTOtoEntity(tripPutDTO);

    // Check content
    assertEquals(tripPutDTO.getTripName(), trip.getTripName());
    assertEquals(tripPutDTO.getTripDescription(), trip.getTripDescription());
    assertEquals(tripPutDTO.getMeetUpTime(), trip.getMeetUpTime());
    assertEquals(tripPutDTO.getMeetUpPlace(), trip.getMeetUpPlace());
    assertEquals(tripPutDTO.getRating(), trip.getRating());
  }

  @Test
  void testConvertEntityToParticipantGetDTO() {
    // Create User
    User user = new User();
    user.setId(123L);
    user.setUsername("test_user");

    // MAP -> Create ParticipantGetDTO
    ParticipantGetDTO participantGetDTO = DTOMapper.INSTANCE.convertEntityToParticipantGetDTO(user);

    // Check content
    assertEquals(user.getId(), participantGetDTO.getId());
    assertEquals(user.getUsername(), participantGetDTO.getUsername());
  }

  @Test
  void testConvertEntityToBasicTripInfoGetDTO() {
    // Create Trip
    Trip trip = new Trip();
    trip.setId(456L);
    trip.setTripName("Test Trip");
    trip.setTripDescription("This is a test trip.");
    trip.setMeetUpTime(LocalDateTime.of(2024, 4, 20, 10, 0));

    // Create Station
    Station station = new Station();
    station.setStationCode("8591122");
    station.setStationName("Zürich, ETH Hönggerberg");
    trip.setMeetUpPlace(station);

    trip.setNumberOfParticipants(5);
    trip.setCompleted(false);
    trip.setRating(4);

    // MAP -> Create BasicTripInfoGetDTO
    BasicTripInfoGetDTO basicTripInfoGetDTO = DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(trip);

    // Check content
    assertEquals(trip.getId(), basicTripInfoGetDTO.getId());
    assertEquals(trip.getTripName(), basicTripInfoGetDTO.getTripName());
    assertEquals(trip.getTripDescription(), basicTripInfoGetDTO.getTripDescription());
    assertEquals(trip.getMeetUpTime(), basicTripInfoGetDTO.getMeetUpTime());
    assertEquals(trip.getMeetUpPlace().getStationCode(), basicTripInfoGetDTO.getMeetUpPlace().getStationCode());
    assertEquals(trip.getMeetUpPlace().getStationName(), basicTripInfoGetDTO.getMeetUpPlace().getStationName());
    assertEquals(trip.getNumberOfParticipants(), basicTripInfoGetDTO.getNumberOfParticipants());
    assertEquals(trip.isCompleted(), basicTripInfoGetDTO.isCompleted());
    assertEquals(trip.getRating(), basicTripInfoGetDTO.getRating());
  }

  @Test
  void testConvertConnectionDTOToEntity() {
    // Create ConnectionDTO
    ConnectionDTO connectionDTO = new ConnectionDTO();
    connectionDTO.setConnectionType(ConnectionType.TRAIN);
    connectionDTO.setConnectionName("IC 123");

    // Create Station
    Station station = new Station();
    station.setStationCode("8591122");
    station.setStationName("Zürich, ETH Hönggerberg");

    connectionDTO.setDepartureTime(LocalDateTime.of(2024,4,17,12,0));
    connectionDTO.setDeparturePoint(station);
    connectionDTO.setArrivalTime(LocalDateTime.of(2024,4,17,16,0));
    connectionDTO.setArrivalPoint(station);

    // MAP -> Create ParticipantConnection
    ParticipantConnection participantConnection = DTOMapper.INSTANCE.convertConnectionDTOToEntity(connectionDTO);

    // Check content
    assertEquals(connectionDTO.getConnectionType(), participantConnection.getConnectionType());
    assertEquals(connectionDTO.getConnectionName(), participantConnection.getConnectionName());
    assertEquals(connectionDTO.getDepartureTime(), participantConnection.getDepartureTime());
    assertEquals(connectionDTO.getDeparturePoint(), participantConnection.getDeparturePoint());
    assertEquals(connectionDTO.getArrivalTime(), participantConnection.getArrivalTime());
    assertEquals(connectionDTO.getArrivalPoint(), participantConnection.getArrivalPoint());
  }

  @Test
  void testConvertEntityToConnectionDTO() {
    // Create ParticipantConnection
    ParticipantConnection participantConnection = new ParticipantConnection();
    participantConnection.setConnectionType(ConnectionType.BUS);
    participantConnection.setConnectionName("Bus 456");

    // Create Station
    Station station = new Station();
    station.setStationCode("8591122");
    station.setStationName("Zürich, ETH Hönggerberg");

    participantConnection.setDepartureTime(LocalDateTime.of(2024,4,17,12,0));
    participantConnection.setDeparturePoint(station);
    participantConnection.setArrivalTime(LocalDateTime.of(2024,4,17,12,0));
    participantConnection.setArrivalPoint(station);

    // MAP -> Create ConnectionDTO
    ConnectionDTO connectionDTO = DTOMapper.INSTANCE.convertEntityToConnectionDTO(participantConnection);

    // Check content
    assertEquals(participantConnection.getConnectionType(), connectionDTO.getConnectionType());
    assertEquals(participantConnection.getConnectionName(), connectionDTO.getConnectionName());
    assertEquals(participantConnection.getDepartureTime(), connectionDTO.getDepartureTime());
    assertEquals(participantConnection.getDeparturePoint(), connectionDTO.getDeparturePoint());
    assertEquals(participantConnection.getArrivalTime(), connectionDTO.getArrivalTime());
    assertEquals(participantConnection.getArrivalPoint(), connectionDTO.getArrivalPoint());
  }

  @Test
  void testConvertToDoPostDTOToEntity() {
    // Create ItemPostDTO
    ItemPostDTO itemPostDTO = new ItemPostDTO();
    itemPostDTO.setItem("Buy groceries");

    // MAP -> Create Item
    Item item = DTOMapper.INSTANCE.convertToDoPostDTOToEntity(itemPostDTO);

    // Check content
    assertEquals(itemPostDTO.getItem(), item.getItem());
  }

  @Test
  void testConvertItemPutDTOToEntity() {
    // Create ItemPutDTO
    ItemPutDTO itemPutDTO = new ItemPutDTO();
    itemPutDTO.setItem("Do laundry");
    itemPutDTO.setCompleted(true);

    // MAP -> Create Item
    Item item = DTOMapper.INSTANCE.convertItemPutDTOToEntity(itemPutDTO);

    // Check content
    assertEquals(itemPutDTO.getItem(), item.getItem());
    assertEquals(itemPutDTO.isCompleted(), item.isCompleted());
  }

  @Test
  void testConvertEntityToItemGetDTO() {
    // Create Item
    Item item = new Item();
    item.setId(1L);
    item.setItem("Study");
    item.setCompleted(false);
    item.setUserId(123L);

    // MAP -> Create ItemGetDTO
    ItemGetDTO itemGetDTO = DTOMapper.INSTANCE.convertEntityToItemGetDTO(item);

    // Check content
    assertEquals(item.getId(), itemGetDTO.getId());
    assertEquals(item.getItem(), itemGetDTO.getItem());
    assertEquals(item.isCompleted(), itemGetDTO.isCompleted());
    assertEquals(item.getUserId(), itemGetDTO.getUserId());
  }

  @Test
  void testConvertUserNotificationToNotificationGetDTO() {
    // Create UserNotification
    UserNotification userNotification = new UserNotification();
    userNotification.setMessage("New message received");
    userNotification.setTimeStamp(LocalDateTime.of(2024,11,11,11,11));

    // MAP -> Create NotificationGetDTO
    NotificationGetDTO notificationGetDTO = DTOMapper.INSTANCE.convertUserNotificationToNotificationGetDTO(userNotification);

    // Check content
    assertEquals(userNotification.getMessage(), notificationGetDTO.getMessage());
    assertEquals(userNotification.getTimeStamp(), notificationGetDTO.getTimeStamp());
  }

  @Test
  void testConvertEntityToNotificationGetDTO() {
    // Create TripNotification
    TripNotification tripNotification = new TripNotification();
    tripNotification.setMessage("New trip created");
    tripNotification.setTimeStamp(LocalDateTime.of(2024,11,11,11,11));

    // MAP -> Create NotificationGetDTO
    NotificationGetDTO notificationGetDTO = DTOMapper.INSTANCE.convertEntityToNotificationGetDTO(tripNotification);

    // Check content
    assertEquals(tripNotification.getMessage(), notificationGetDTO.getMessage());
    assertEquals(tripNotification.getTimeStamp(), notificationGetDTO.getTimeStamp());
  }

  @Test
  void testConvertEntityToMemberGetDTO() {
    // Create User
    User user = new User();
    user.setId(1L);
    user.setUsername("john_doe");
    Image profileImage = new Image();
    byte[] byteArray = "profile_picture_bytes".getBytes();
    profileImage.setProfilePicture(byteArray);
    user.setProfileImage(profileImage);

    // MAP -> Create MemberGetDTO
    MemberGetDTO memberGetDTO = DTOMapper.INSTANCE.convertEntityToMemberGetDTO(user);

    // Check content
    assertEquals(user.getId(), memberGetDTO.getId());
    assertEquals(user.getUsername(), memberGetDTO.getUsername());
    assertArrayEquals(user.getProfileImage().getProfilePicture(), memberGetDTO.getProfilePicture());
  }

  @Test
  void testConvertTemplateDTOToEntity() {
    // Create TemplateDTO
    TemplateDTO templateDTO = new TemplateDTO();
    templateDTO.setItem("Toothbrush");

    // MAP -> Create TemplatePackingItem
    TemplatePackingItem templatePackingItem = DTOMapper.INSTANCE.convertTemplateDTOToEntity(templateDTO);

    // Check content
    assertEquals(templateDTO.getItem(), templatePackingItem.getItem());
  }

  @Test
  void testConvertEntityToTemplateGetDTO() {
    // Create TemplatePackingItem
    TemplatePackingItem templatePackingItem = new TemplatePackingItem();
    templatePackingItem.setId(1L);
    templatePackingItem.setItem("Phone charger");

    // MAP -> Create TemplateGetDTO
    TemplateGetDTO templateGetDTO = DTOMapper.INSTANCE.convertEntityToTemplateGetDTO(templatePackingItem);

    // Check content
    assertEquals(templatePackingItem.getId(), templateGetDTO.getId());
    assertEquals(templatePackingItem.getItem(), templateGetDTO.getItem());
  }
}
