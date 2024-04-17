package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.FriendshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class FriendshipServiceTest {

  @Mock
  private FriendshipRepository friendshipRepository;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private FriendshipService friendshipService;

  private User testUser1;
  private User testUser2;
  private Friendship testFriendship;

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

    testFriendship = new Friendship();
    testFriendship.setFriend1(testUser1);
    testFriendship.setFriend2(testUser2);
    testFriendship.setStatus(FriendShipStatus.PENDING);
    testFriendship.setPoints(0);
    testFriendship.setId(1L);

    Mockito.when(friendshipRepository.save(Mockito.any())).thenReturn(testFriendship);
  }

  @Test
  public void testSendRequest_success() {
    // when
    friendshipService.sendRequest(testUser1, testUser2);

    // then
    Mockito.verify(friendshipRepository, Mockito.times(1)).save(Mockito.any());
    Mockito.verify(notificationService, Mockito.times(1))
            .createUserNotification(eq(testUser1), Mockito.anyString());
    Mockito.verify(notificationService, Mockito.times(1))
            .createUserNotification(eq(testUser2), Mockito.anyString());
  }
  @Test
  public void testSendRequest_toYourself_throwsError() {
    assertThrows(ResponseStatusException.class, () -> friendshipService.sendRequest(testUser1, testUser1));
  }

  @Test
  public void testSendRequest_duplicate_throwsError() {
    // given
    friendshipService.sendRequest(testUser1, testUser2);
    // when
    Mockito.when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
    // then
    assertThrows(ResponseStatusException.class, () -> friendshipService.sendRequest(testUser1, testUser2));
  }

  @Test
  public void testAcceptRequest_success() {
    // when
    Mockito.when(friendshipRepository.findByFriend1AndFriend2(Mockito.any(), Mockito.any())).thenReturn(testFriendship);
    friendshipService.acceptRequest(testUser2, testUser1);

    // then
    assertEquals(FriendShipStatus.ACCEPTED, testFriendship.getStatus());
    Mockito.verify(friendshipRepository, Mockito.times(1)).save(testFriendship);
    Mockito.verify(friendshipRepository, Mockito.times(1)).flush();
    Mockito.verify(notificationService, Mockito.times(1))
            .createUserNotification(eq(testUser1), Mockito.anyString());
    Mockito.verify(notificationService, Mockito.times(1))
            .createUserNotification(eq(testUser2), Mockito.anyString());
  }

  @Test
  public void testAcceptRequest_nonExistentFriendship_throwsError() {
    // when
    Mockito.when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(null);
    // then
    assertThrows(ResponseStatusException.class, () -> friendshipService.acceptRequest(testUser2, testUser1));
  }
  @Test
  public void testDeleteFriend_success() {
    // given
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);

    // when
    friendshipService.deleteFriend(testUser2, testUser1);

    // then
    verify(friendshipRepository, times(1)).delete(testFriendship);
    verify(notificationService, times(1))
            .createUserNotification(eq(testUser1), Mockito.anyString());
    verify(notificationService, times(1))
            .createUserNotification(eq(testUser2), Mockito.anyString());
  }

  @Test
  public void testDeleteFriend_noFriendship_throwsNotFoundError() {
    // given
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(null);

    // then
    assertThrows(ResponseStatusException.class, () -> friendshipService.deleteFriend(testUser2, testUser1));
  }

  @Test
  public void testDeleteFriend_conflict_throwsConflictError() {
    // given
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
    when(friendshipRepository.findByFriend1AndFriend2(testUser2, testUser1)).thenReturn(testFriendship);

    // then
    assertThrows(ResponseStatusException.class, () -> friendshipService.deleteFriend(testUser2, testUser1));
  }
}
