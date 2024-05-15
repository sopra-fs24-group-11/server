package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.assertj.core.api.PathAssert;
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

class NullCheckerIntegrationTest {

  @Test
  void userPostDTOChecker_PasswordNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPostDTOChecker_UsernameNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPostDTOChecker_UsernameLengthLessThan2_ThrowsConflictException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("a");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPostDTOChecker_UsernameLengthMoreThan30_ThrowsConflictException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username1234567890123456789012345678901"); // 31 characters
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPostDTOChecker_EmailNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPostDTOChecker_BirthdayNull_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPostDTOChecker_InvalidEmail_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("invalidemail@email");
    dto.setBirthday(LocalDate.now().minusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPostDTOChecker_BirthdayInTheFuture_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    dto.setBirthday(LocalDate.now().plusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userLoginPostDTOChecker_PasswordNull_ThrowsBadRequestException() {
    UserLoginPostDTO dto = new UserLoginPostDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.userLoginPostDTOChecker(dto));
  }

  @Test
  void userLoginPostDTOChecker_UsernameNull_ThrowsBadRequestException() {
    UserLoginPostDTO dto = new UserLoginPostDTO();
    dto.setPassword("password");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userLoginPostDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_PasswordNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_UsernameNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_UsernameLengthLessThan2_ThrowsConflictException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setUsername("a");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_UsernameLengthMoreThan30_ThrowsConflictException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setUsername("username1234567890123456789012345678901"); // 31 characters
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_EmailNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setUsername("username");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_InvalidEmail_ThrowsBadRequestException() {
    UserPostDTO dto = new UserPostDTO();
    dto.setPassword("password");
    dto.setUsername("username");
    dto.setEmail("invalidemail@email");
    dto.setBirthday(LocalDate.now().minusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPostDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_BirthdayNull_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  void userPutDTOChecker_BirthdayInTheFuture_ThrowsBadRequestException() {
    UserPutDTO dto = new UserPutDTO();
    dto.setUsername("username");
    dto.setEmail("email@example.com");
    dto.setBirthday(LocalDate.now().plusDays(1));
    assertThrows(ResponseStatusException.class, () -> NullChecker.userPutDTOChecker(dto));
  }

  @Test
  void passwordPutDTOChecker_Password1Null_ThrowsBadRequestException() {
    PasswordPutDTO dto = new PasswordPutDTO();
    dto.setPassword(null);
    dto.setPassword2("password");
    assertThrows(ResponseStatusException.class, () -> NullChecker.passwordPutDTOChecker(dto));
  }
  @Test
  void passwordPutDTOChecker_Password2Null_ThrowsBadRequestException() {
    PasswordPutDTO dto = new PasswordPutDTO();
    dto.setPassword("password");
    dto.setPassword2(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.passwordPutDTOChecker(dto));
  }
  @Test
  void passwordPutDTOChecker_PasswordUnEqual_ThrowsBadRequestException() {
    PasswordPutDTO dto = new PasswordPutDTO();
    dto.setPassword("password1");
    dto.setPassword2("password2");
    assertThrows(ResponseStatusException.class, () -> NullChecker.passwordPutDTOChecker(dto));
  }
  @Test
  void passwordPutDTOChecker_PasswordTooShort_ThrowsBadRequestException() {
    PasswordPutDTO dto = new PasswordPutDTO();
    dto.setPassword("p");
    dto.setPassword2("p");
    assertThrows(ResponseStatusException.class, () -> NullChecker.passwordPutDTOChecker(dto));
  }

  @Test
  void messagePostDTOChecker_MessageNull_ThrowsBadRequestException() {
    MessagePostDTO dto = new MessagePostDTO();
    assertThrows(ResponseStatusException.class, () -> NullChecker.messagePostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_NullTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName(null);
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_ShortTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("A");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_LongTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("ThisIsAReallyLongTripNameThatExceedsTheMaximumLengthAllowed");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_BlankTripName_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("    ");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_NullTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription(null);
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_ShortTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription("A");
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_LongTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription("This is a very long trip description that exceeds the maximum allowed length of 200 characters. This is a very long trip description that exceeds the maximum allowed length of 200 characters. This is a very long trip description that exceeds the maximum allowed length of 200 characters. This is a very long trip description that exceeds the maximum allowed length of 200 characters.");
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_BlankTripDescription_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripDescription("    ");
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_NullMeetUpPlace_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpPlace(null);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_NullMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName(null);
    station.setStationCode(null);
    dto.setMeetUpPlace(station);
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_BlankMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName("");
    station.setStationCode("");
    dto.setMeetUpPlace(station);
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_NullMeetUpTime_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(null);
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_PastMeetUpTime_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().minusDays(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_NullParticipantsList_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    dto.setParticipants(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPostDTOChecker_NullParticipantId_ThrowsBadRequestException() {
    TripPostDTO dto = new TripPostDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(null);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPostDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_NullTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName(null);
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_ShortTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("A");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_LongTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("ThisIsAReallyLongTripNameThatExceedsTheMaximumLengthAllowed");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_BlankTripName_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("    ");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_NullTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription(null);
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }
  @Test
  void tripPutDTOChecker_ShortTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription("A");
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_LongTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription("This is a very long trip description that exceeds the maximum allowed length of 200 characters. This is a very long trip description that exceeds the maximum allowed length of 200 characters. This is a very long trip description that exceeds the maximum allowed length of 200 characters. This is a very long trip description that exceeds the maximum allowed length of 200 characters.");
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_BlankTripDescription_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripDescription("    ");
    dto.setTripName("Holidays");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_NullMeetUpPlace_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpPlace(null);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_NullMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName(null);
    station.setStationCode(null);
    dto.setMeetUpPlace(station);
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_BlankMeetUpPlaceInfo_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName("");
    station.setStationCode("");
    dto.setMeetUpPlace(station);
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_NullMeetUpTime_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(null);
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_PastMeetUpTime_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().minusDays(1));
    List<Long> participants = new ArrayList<>();
    participants.add(1L);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_NullParticipantsList_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    dto.setParticipants(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void tripPutDTOChecker_NullParticipantId_ThrowsBadRequestException() {
    TripPutDTO dto = new TripPutDTO();
    dto.setTripName("Holidays");
    dto.setTripDescription("Description");
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setMeetUpPlace(station);
    dto.setMeetUpTime(LocalDateTime.now().plusHours(1));
    List<Long> participants = new ArrayList<>();
    participants.add(null);
    dto.setParticipants(participants);
    assertThrows(ResponseStatusException.class, () -> NullChecker.tripPutDTOChecker(dto));
  }

  @Test
  void connectionDTOsChecker_NullConnectionType_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullConnectionName_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullDepartureTime_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullDeparturePoint_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now().plusHours(1));
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullDepartureStationName_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName(null);
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullDepartureStationCode_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode(null);
    dto.setDeparturePoint(station);
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullArrivalTime_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullArrivalPoint_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    dto.setArrivalTime(LocalDateTime.now().plusHours(1));
    dto.setArrivalPoint(null);
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullArrivalStationName_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now().plusHours(1));
    Station station = new Station();
    station.setStationName("Station Name");
    station.setStationCode("ABC");
    dto.setDeparturePoint(station);
    dto.setArrivalTime(LocalDateTime.now().plusHours(1));
    Station station2 = new Station();
    station2.setStationName(null);
    station2.setStationCode("XYZ");
    dto.setArrivalPoint(station2);
    List<ConnectionDTO> connectionDTOS = new ArrayList<>(); connectionDTOS.add(dto);
    assertThrows(ResponseStatusException.class, () -> NullChecker.connectionDTOsChecker(connectionDTOS));
  }

  @Test
  void connectionDTOsChecker_NullArrivalStationCode_ThrowsBadRequestException() {
    ConnectionDTO dto = new ConnectionDTO();
    dto.setConnectionType(ConnectionType.TRAIN);
    dto.setConnectionName("Example Connection");
    dto.setDepartureTime(LocalDateTime.now().plusHours(1));
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
  void itemPostDTOChecker_NullItem_ThrowsBadRequestException() {
    ItemPostDTO dto = new ItemPostDTO();
    dto.setItem(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.itemPostDTOChecker(dto));
  }

  @Test
  void itemPostDTOChecker_EmptyItem_ThrowsBadRequestException() {
    ItemPostDTO dto = new ItemPostDTO();
    dto.setItem("");
    assertThrows(ResponseStatusException.class, () -> NullChecker.itemPostDTOChecker(dto));
  }

  @Test
  void itemPostDTOChecker_TooLongItem_ThrowsBadRequestException() {
    ItemPostDTO dto = new ItemPostDTO();
    dto.setItem("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890");
    assertThrows(ResponseStatusException.class, () -> NullChecker.itemPostDTOChecker(dto));
  }


  @Test
  void templateDTOChecker_NullItem_ThrowsBadRequestException() {
    TemplateDTO dto = new TemplateDTO();
    dto.setItem(null);
    assertThrows(ResponseStatusException.class, () -> NullChecker.templateDTOChecker(dto));
  }

}