package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.FriendshipRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TripParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TripParticipantServiceTest {

  @Mock
  private TripParticipantRepository tripParticipantRepository;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private TripParticipantService tripParticipantService;

  private User testUser1;
  private User testUser2;
  private Trip testTrip1;
  private TripParticipant testParticipant;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser1 = new User();
    testUser1.setId(1L);
    testUser1.setPassword("user1");
    testUser1.setUsername("user1");

    testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setPassword("user2");
    testUser2.setUsername("user2");

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

    testParticipant = new TripParticipant();
    testParticipant.setTrip(testTrip1);
    testParticipant.setStatus(InvitationStatus.PENDING);
    testParticipant.setInvitator(testUser1);
    testParticipant.setUser(testUser2);

    Mockito.when(tripParticipantRepository.save(Mockito.any())).thenReturn(testParticipant);
  }

  @Test
  public void testStoreParticipant_success() {
    // when
    tripParticipantService.storeParticipant(testTrip1, testUser1, testUser2);

    // then
    Mockito.verify(tripParticipantRepository, Mockito.times(1)).save(Mockito.any());
    Mockito.verify(notificationService, Mockito.times(1))
            .createUserNotification(Mockito.any(), Mockito.anyString());
    Mockito.verify(notificationService, Mockito.times(1))
            .createTripNotification(Mockito.any(), Mockito.anyString());
  }


}
