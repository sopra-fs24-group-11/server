package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
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
