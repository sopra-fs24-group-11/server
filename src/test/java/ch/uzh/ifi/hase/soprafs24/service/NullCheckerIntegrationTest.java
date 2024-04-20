package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class NullCheckerIntegrationTest {

  @Test
  public void userPostDTOChecker_PasswordNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  public void userPostDTOChecker_UsernameNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  public void userPostDTOChecker_UsernameLengthLessThan2_ThrowsConflictException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("a");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  public void userPostDTOChecker_UsernameLengthMoreThan30_ThrowsConflictException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username1234567890123456789012345678901"); // 31 characters
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  public void userPostDTOChecker_EmailNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  public void userPostDTOChecker_BirthdayNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  public void userPostDTOChecker_BirthdayInTheFuture_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    dto.setBirthday(LocalDate.now().plusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  public void userLoginPostDTOChecker_PasswordNull_ThrowsBadRequestException() {
    UserLoginPostDTO dto = new UserLoginPostDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.userLoginPostDTOChecker(dto));
  }

  @Test
  public void userLoginPostDTOChecker_UsernameNull_ThrowsBadRequestException() {
    UserLoginPostDTO dto = new UserLoginPostDTO();
    dto.setPassword("password");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userLoginPostDTOChecker(dto));
  }

  @Test
  public void userPutDTOChecker_PasswordNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  public void userPutDTOChecker_UsernameNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setPassword("password");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  public void userPutDTOChecker_UsernameLengthLessThan2_ThrowsConflictException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setPassword("password");
    dto.setUsername("a");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  public void userPutDTOChecker_UsernameLengthMoreThan30_ThrowsConflictException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setPassword("password");
    dto.setUsername("username1234567890123456789012345678901"); // 31 characters
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  public void userPutDTOChecker_EmailNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  public void userPutDTOChecker_BirthdayNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  public void userPutDTOChecker_BirthdayInTheFuture_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    dto.setBirthday(LocalDate.now().plusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  public void messagePostDTOChecker_MessageNull_ThrowsBadRequestException() {
    MessagePostDTO dto = new MessagePostDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.messagePostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_NullTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName(null);
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_ShortTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("A");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_LongTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("ThisIsAReallyLongTripNameThatExceedsTheMaximumLengthAllowed");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_BlankTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("    ");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_InvalidTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Trip@Name");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_NullTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription(null);
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_ShortTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription("A");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_LongTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription("This is a very long trip description that exceeds the maximum allowed length of 50 characters.");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_BlankTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription("    ");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_InvalidTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription("Description@123");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_NullMeetUpPlace_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setMeetUpPlace(null);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_NullMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName(null);
    station.setStationCode(null);
    dto.setMeetUpPlace(station);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_BlankMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("");
    station.setStationCode("");
    dto.setMeetUpPlace(station);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_NullMeetUpTime_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_PastMeetUpTime_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().minusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_NullParticipantsList_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setParticipants(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPostDTOChecker_NullParticipantId_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    List<Long> participants = new ArrayList<>();
    participants.add(null);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_NullTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName(null);
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_ShortTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("A");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_LongTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("ThisIsAReallyLongTripNameThatExceedsTheMaximumLengthAllowed");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_BlankTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("    ");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_InvalidTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Trip@Name");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_NullTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription(null);
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }
  @Test
  public void tripPutDTOChecker_ShortTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription("A");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_LongTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription("This is a very long trip description that exceeds the maximum allowed length of 50 characters.");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_BlankTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription("    ");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_InvalidCharactersInTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription("!@#");
    dto.setTripName("Holidays");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_NullMeetUpPlace_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setMeetUpPlace(null);
    dto.setMeetUpTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_NullMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName(null);
    station.setStationCode(null);
    dto.setMeetUpPlace(station);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_BlankMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("");
    station.setStationCode("");
    dto.setMeetUpPlace(station);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_NullMeetUpTime_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_PastMeetUpTime_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().minusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_NullParticipantsList_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    dto.setParticipants(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void tripPutDTOChecker_NullParticipantId_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now());
    List<Long> participants = new ArrayList<>();
    participants.add(null);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  public void connectionDTOsChecker_NullConnectionType_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullConnectionName_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullDepartureTime_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullDeparturePoint_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now());
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullDepartureStationName_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName(null);
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullDepartureStationCode_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode(null);
    dto.setDeparturePoint(station);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullArrivalTime_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullArrivalPoint_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    dto.setArrivalTime(LocalDateTime.now().plusHours(1));
    dto.setArrivalPoint(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullArrivalStationName_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    dto.setArrivalTime(LocalDateTime.now().plusHours(1));
    Station station2 = new Station();
    station2.setStationName(null);
    station2.setStationCode("XYZ");
    dto.setArrivalPoint(station2);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void connectionDTOsChecker_NullArrivalStationCode_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now());
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    dto.setArrivalTime(LocalDateTime.now().plusHours(1));
    Station station2 = new Station();
    station2.setStationName("Station Name");
    station2.setStationCode(null);
    dto.setArrivalPoint(station2);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(Collections.singletonList(dto)));
  }

  @Test
  public void itemPostDTOChecker_NullItem_ThrowsBadRequestException() {
    ItemPostDTO dto = new ItemPostDTO();
    dto.setItem(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.itemPostDTOChecker(dto));
  }

  @Test
  public void templateDTOChecker_NullItem_ThrowsBadRequestException() {
    TemplateDTO dto = new TemplateDTO();
    dto.setItem(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.templateDTOChecker(dto));
  }

}