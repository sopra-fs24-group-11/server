package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TripController.class)
public class TripControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private TripService tripService;
  @MockBean
  private UserService userService;
  @MockBean
  private TripParticipantService tripParticipantService;
  @MockBean
  private ListService listService;
  @MockBean
  private ConnectionService connectionService;
  @MockBean
  private NotificationService notificationService;

  private User testUser;
  private Trip testTrip;
  private TripParticipant testTripParticipant;
  private ParticipantConnection testParticipantConnection;
  private TripNotification testTripNotification;
  private Item testTodoItem;
  private Item testIndividualItem;
  private Item testGroupItem;
  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("user@test.ch");
    testUser.setBirthday(LocalDate.of(2000, 1, 1));
    testUser.setToken("1d");
    testUser.setStatus(UserStatus.ONLINE);
    testUser.setLastOnline(LocalDateTime.of(2030, 11, 11, 11, 11));

    testTrip = new Trip();
    testTrip.setId(1L);
    testTrip.setTripName("Ferien");
    testTrip.setTripDescription("Ferien nach Como");
    testTrip.setAdministrator(testUser);
    testTrip.setNumberOfParticipants(1);
    testTrip.setCompleted(false);
    Station station = new Station();
    station.setStationCode("8301307");
    station.setStationName("Como S. Giovanni");
    testTrip.setMeetUpPlace(station);
    testTrip.setMeetUpTime(LocalDateTime.of(2030, 11, 11, 11, 11));
    testTrip.setMaxParticipants(10);

    testTripParticipant = new TripParticipant();
    testTripParticipant.setId(1L);
    testTripParticipant.setUser(testUser);
    testTripParticipant.setTrip(testTrip);
    testTripParticipant.setFavouriteTrip(true);
    testTripParticipant.setStatus(InvitationStatus.ACCEPTED);
    testTripParticipant.setInvitator(testUser);

    testParticipantConnection = new ParticipantConnection();
    testParticipantConnection.setParticipant(testTripParticipant);
    testParticipantConnection.setDepartureTime(LocalDateTime.of(2024,11,11,11,11));
    testParticipantConnection.setArrivalTime(LocalDateTime.of(2024,11,11,11,15));
    testParticipantConnection.setConnectionType(ConnectionType.BUS);
    testParticipantConnection.setConnectionName("B 817");
    Station departurePoint = new Station(); departurePoint.setStationCode("8503633"); departurePoint.setStationName("Uster, See");
    testParticipantConnection.setDeparturePoint(departurePoint);
    Station arrivalPoint = new Station(); arrivalPoint.setStationCode("8573504"); arrivalPoint.setStationName("Uster, Bahnhof");
    testParticipantConnection.setArrivalPoint(arrivalPoint);

    testTripNotification = new TripNotification();
    testTripNotification.setTrip(testTrip);
    testTripNotification.setId(1L);
    testTripNotification.setMessage("This is a trip notification.");
    testTripNotification.setTimeStamp(LocalDateTime.of(2024,11,11,11,11));

    testTodoItem = new Item();
    testTodoItem.setId(1L);
    testTodoItem.setItemType(ItemType.TODO);
    testTodoItem.setItem("Hotel reservation");
    testTodoItem.setCompleted(false);
    testTodoItem.setUserId(testUser.getId());
    testTodoItem.setTrip(testTrip);
    testTodoItem.setParticipant(testTripParticipant);

    testIndividualItem = new Item();
    testIndividualItem.setId(2L);
    testIndividualItem.setItemType(ItemType.INDIVIDUALPACKING);
    testIndividualItem.setItem("Shirt");
    testIndividualItem.setCompleted(false);
    testIndividualItem.setUserId(testUser.getId());
    testIndividualItem.setTrip(testTrip);
    testIndividualItem.setParticipant(testTripParticipant);

    testGroupItem = new Item();
    testGroupItem.setId(3L);
    testGroupItem.setItemType(ItemType.GROUPPACKING);
    testGroupItem.setItem("Car");
    testGroupItem.setCompleted(false);
    testGroupItem.setUserId(testUser.getId());
    testGroupItem.setTrip(testTrip);
    testGroupItem.setParticipant(testTripParticipant);


  }
  // GET REQUESTS ----------------------------------------------------------------------------------------------------------------
  @Test
  public void getTripInfo_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    given(tripParticipantService.getTripParticipant(testTrip, testUser)).willReturn(testTripParticipant);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(testTrip.getId().intValue())))
            .andExpect(jsonPath("$.numberOfParticipants", is(testTrip.getNumberOfParticipants())))
            .andExpect(jsonPath("$.tripName", is(testTrip.getTripName())))
            .andExpect(jsonPath("$.tripDescription", is(testTrip.getTripDescription())))
            .andExpect(jsonPath("$.rating", is(testTrip.getRating())))
            .andExpect(jsonPath("$.meetUpPlace.stationCode", is(testTrip.getMeetUpPlace().getStationCode())))
            .andExpect(jsonPath("$.meetUpPlace.stationName", is(testTrip.getMeetUpPlace().getStationName())))
            .andExpect(jsonPath("$.meetUpTime", is(testTrip.getMeetUpTime().format(formatter))))
            .andExpect(jsonPath("$.favourite", is(testTripParticipant.isFavouriteTrip())));
  }


  @Test
  public void getTripParticipants_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    given(tripParticipantService.getTripParticipant(testTrip, testUser)).willReturn(testTripParticipant);
    List<User> users = new ArrayList<>(); users.add(testUser);
    given(tripParticipantService.getTripUsersWithoutAdmin(testTrip)).willReturn(users);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/participants", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(testUser.getId().intValue())))
            .andExpect(jsonPath("$[0].username", is(testUser.getUsername())));
  }

  @Test
  public void getTripHistory_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    List<TripParticipant> participants = new ArrayList<>(); participants.add(testTripParticipant);
    given(tripParticipantService.getTripHistory(testUser)).willReturn(participants);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/history")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(testTrip.getId().intValue())))
            .andExpect(jsonPath("$[0].numberOfParticipants", is(testTrip.getNumberOfParticipants())))
            .andExpect(jsonPath("$[0].tripName", is(testTrip.getTripName())))
            .andExpect(jsonPath("$[0].tripDescription", is(testTrip.getTripDescription())))
            .andExpect(jsonPath("$[0].rating", is(testTrip.getRating())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationCode", is(testTrip.getMeetUpPlace().getStationCode())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationName", is(testTrip.getMeetUpPlace().getStationName())))
            .andExpect(jsonPath("$[0].meetUpTime", is(testTrip.getMeetUpTime().format(formatter))))
            .andExpect(jsonPath("$[0].favourite", is(testTripParticipant.isFavouriteTrip())));
  }

  @Test
  public void getCurrentTrips_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    List<TripParticipant> participants = new ArrayList<>(); participants.add(testTripParticipant);
    given(tripParticipantService.getCurrentTrips(testUser)).willReturn(participants);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/current")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(testTrip.getId().intValue())))
            .andExpect(jsonPath("$[0].numberOfParticipants", is(testTrip.getNumberOfParticipants())))
            .andExpect(jsonPath("$[0].tripName", is(testTrip.getTripName())))
            .andExpect(jsonPath("$[0].tripDescription", is(testTrip.getTripDescription())))
            .andExpect(jsonPath("$[0].rating", is(testTrip.getRating())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationCode", is(testTrip.getMeetUpPlace().getStationCode())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationName", is(testTrip.getMeetUpPlace().getStationName())))
            .andExpect(jsonPath("$[0].meetUpTime", is(testTrip.getMeetUpTime().format(formatter))))
            .andExpect(jsonPath("$[0].favourite", is(testTripParticipant.isFavouriteTrip())));
  }

  @Test
  public void getUnansweredTrips_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    List<TripParticipant> participants = new ArrayList<>(); participants.add(testTripParticipant);
    given(tripParticipantService.getUnansweredTrips(testUser)).willReturn(participants);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/invitations")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(testTrip.getId().intValue())))
            .andExpect(jsonPath("$[0].numberOfParticipants", is(testTrip.getNumberOfParticipants())))
            .andExpect(jsonPath("$[0].tripName", is(testTrip.getTripName())))
            .andExpect(jsonPath("$[0].tripDescription", is(testTrip.getTripDescription())))
            .andExpect(jsonPath("$[0].rating", is(testTrip.getRating())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationCode", is(testTrip.getMeetUpPlace().getStationCode())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationName", is(testTrip.getMeetUpPlace().getStationName())))
            .andExpect(jsonPath("$[0].meetUpTime", is(testTrip.getMeetUpTime().format(formatter))))
            .andExpect(jsonPath("$[0].favourite", is(testTripParticipant.isFavouriteTrip())));
  }

  @Test
  public void getFavoriteTrips_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    List<TripParticipant> participants = new ArrayList<>(); participants.add(testTripParticipant);
    given(tripParticipantService.getFavoriteTrips(testUser)).willReturn(participants);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/favorites")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(testTrip.getId().intValue())))
            .andExpect(jsonPath("$[0].numberOfParticipants", is(testTrip.getNumberOfParticipants())))
            .andExpect(jsonPath("$[0].tripName", is(testTrip.getTripName())))
            .andExpect(jsonPath("$[0].tripDescription", is(testTrip.getTripDescription())))
            .andExpect(jsonPath("$[0].rating", is(testTrip.getRating())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationCode", is(testTrip.getMeetUpPlace().getStationCode())))
            .andExpect(jsonPath("$[0].meetUpPlace.stationName", is(testTrip.getMeetUpPlace().getStationName())))
            .andExpect(jsonPath("$[0].meetUpTime", is(testTrip.getMeetUpTime().format(formatter))))
            .andExpect(jsonPath("$[0].favourite", is(testTripParticipant.isFavouriteTrip())));
  }

// Problem below: I think it's that the method is static.
  /*@Test
  public void getStations_success() throws Exception {
    // given
    String name = "Como S. Giovanni";
    Station station = new Station();
    station.setStationCode("8301307");
    station.setStationName(name);
    List<Station> stations = new ArrayList<>(); stations.add(station);
    given(ConnectionService.getLocationsName(name)).willReturn(stations);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/searchStation")
            .param("start", name)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].stationName", is(station.getStationName())))
            .andExpect(jsonPath("$[0].stationCode", is(station.getStationCode())));

  }*/


  @Test
  public void isAdmin_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    given(tripService.isAdmin(testTrip, testUser)).willReturn(true);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/admin", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$", is(true)));
  }


  @Test
  public void getMembersWithImages_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    List<User> users = new ArrayList<>(); users.add(testUser);
    given(tripParticipantService.getTripUsersWhoHaveAccepted(testTrip)).willReturn(users);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/pictures", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(testUser.getId().intValue())))
            .andExpect(jsonPath("$[0].username", is(testUser.getUsername())));
  }

  @Test
  public void getConnection_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    given(tripParticipantService.getTripParticipant(testTrip, testUser)).willReturn(testTripParticipant);
    List<ParticipantConnection> connections = new ArrayList<>(); connections.add(testParticipantConnection);
    given(connectionService.getConnection(testTripParticipant)).willReturn(connections);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/connection", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].connectionType", is(testParticipantConnection.getConnectionType().toString())))
            .andExpect(jsonPath("$[0].connectionName", is(testParticipantConnection.getConnectionName())))
            .andExpect(jsonPath("$[0].departureTime", is(testParticipantConnection.getDepartureTime().format(formatter))))
            .andExpect(jsonPath("$[0].arrivalTime", is(testParticipantConnection.getArrivalTime().format(formatter))))
            .andExpect(jsonPath("$[0].departurePoint.stationCode", is(testParticipantConnection.getDeparturePoint().getStationCode())))
            .andExpect(jsonPath("$[0].departurePoint.stationName", is(testParticipantConnection.getDeparturePoint().getStationName())))
            .andExpect(jsonPath("$[0].arrivalPoint.stationCode", is(testParticipantConnection.getArrivalPoint().getStationCode())))
            .andExpect(jsonPath("$[0].arrivalPoint.stationName", is(testParticipantConnection.getArrivalPoint().getStationName())));
  }

  @Test
  public void getConnections_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    List<TripParticipant> participants = new ArrayList<>(); participants.add(testTripParticipant);
    given(tripParticipantService.getTripParticipantsWhoHaveAccepted(testTrip)).willReturn(participants);
    List<ParticipantConnection> connections = new ArrayList<>(); connections.add(testParticipantConnection);
    given(connectionService.getConnection(testTripParticipant)).willReturn(connections);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/connections", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username", is(testUser.getUsername())))
            .andExpect(jsonPath("$[0].connectionDTO[0].connectionType", is(testParticipantConnection.getConnectionType().toString())))
            .andExpect(jsonPath("$[0].connectionDTO[0].connectionName", is(testParticipantConnection.getConnectionName())))
            .andExpect(jsonPath("$[0].connectionDTO[0].departureTime", is(testParticipantConnection.getDepartureTime().format(formatter))))
            .andExpect(jsonPath("$[0].connectionDTO[0].arrivalTime", is(testParticipantConnection.getArrivalTime().format(formatter))))
            .andExpect(jsonPath("$[0].connectionDTO[0].departurePoint.stationCode", is(testParticipantConnection.getDeparturePoint().getStationCode())))
            .andExpect(jsonPath("$[0].connectionDTO[0].departurePoint.stationName", is(testParticipantConnection.getDeparturePoint().getStationName())))
            .andExpect(jsonPath("$[0].connectionDTO[0].arrivalPoint.stationCode", is(testParticipantConnection.getArrivalPoint().getStationCode())))
            .andExpect(jsonPath("$[0].connectionDTO[0].arrivalPoint.stationName", is(testParticipantConnection.getArrivalPoint().getStationName())));
  }

  @Test
  public void getTripNotifications_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    List<TripNotification> notifications = new ArrayList<>(); notifications.add(testTripNotification);
    given(notificationService.getTripNotifications(testTrip)).willReturn(notifications);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/notifications", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].message", is(testTripNotification.getMessage())))
            .andExpect(jsonPath("$[0].timeStamp", is(testTripNotification.getTimeStamp().format(formatter))));
  }

  @Test
  public void getTodos_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    given(tripParticipantService.getTripParticipant(testTrip, testUser)).willReturn(testTripParticipant);
    List<Item> items = new ArrayList<>(); items.add(testTodoItem);
    given(listService.getItems(testTrip, ItemType.TODO, testTripParticipant)).willReturn(items);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/todos", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].completed", is(testTodoItem.isCompleted())))
            .andExpect(jsonPath("$[0].item", is(testTodoItem.getItem())))
            .andExpect(jsonPath("$[0].id", is(testTodoItem.getId().intValue())))
            .andExpect(jsonPath("$[0].userId", is(testUser.getId().intValue())));
  }

  @Test
  public void getGroupPackings_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    given(tripParticipantService.getTripParticipant(testTrip, testUser)).willReturn(testTripParticipant);
    List<Item> items = new ArrayList<>(); items.add(testGroupItem);
    given(listService.getItems(testTrip, ItemType.GROUPPACKING, testTripParticipant)).willReturn(items);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/groupPackings", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].completed", is(testTodoItem.isCompleted())))
            .andExpect(jsonPath("$[0].item", is(testGroupItem.getItem())))
            .andExpect(jsonPath("$[0].id", is(testGroupItem.getId().intValue())))
            .andExpect(jsonPath("$[0].userId", is(testUser.getId().intValue())));
  }

  @Test
  public void getIndividualPackings_success() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(tripService.getTripById(testTrip.getId())).willReturn(testTrip);
    given(tripParticipantService.getTripParticipant(testTrip, testUser)).willReturn(testTripParticipant);
    List<Item> items = new ArrayList<>(); items.add(testIndividualItem);
    given(listService.getItems(testTrip, ItemType.INDIVIDUALPACKING, testTripParticipant)).willReturn(items);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/trips/{tripId}/individualPackings", testTrip.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].completed", is(testIndividualItem.isCompleted())))
            .andExpect(jsonPath("$[0].item", is(testIndividualItem.getItem())))
            .andExpect(jsonPath("$[0].id", is(testIndividualItem.getId().intValue())))
            .andExpect(jsonPath("$[0].userId", is(testUser.getId().intValue())));
  }


// POST REQUESTS ----------------------------------------------------------------------------------------------------------------









  private String asJsonString(final Object object) {
    try {
      // Create ObjectMapper instance
      ObjectMapper objectMapper = new ObjectMapper();

      // Configure ObjectMapper to use the desired date format
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      objectMapper.setDateFormat(dateFormat);

      // Register JavaTimeModule for LocalDate serialization
      objectMapper.registerModule(new JavaTimeModule());

      // Convert object to JSON string
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              String.format("The request body could not be created.%s", e.toString()));
    }
  }

}
