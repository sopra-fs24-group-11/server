package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;
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
  private User testUser3;
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

    testUser3 = new User();
    testUser3.setId(3L);
    testUser3.setPassword("user3");
    testUser3.setUsername("user3");

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
    Mockito.when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);

    // then
    assertThrows(ResponseStatusException.class, () -> friendshipService.sendRequest(testUser1, testUser2));
  }

  @Test
  public void testAcceptRequest_success() {
    // when
    Mockito.when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
    friendshipService.acceptRequest(testUser2, testUser1);

    // then
    assertEquals(FriendShipStatus.ACCEPTED, testFriendship.getStatus());
    verify(friendshipRepository, times(1)).save(testFriendship);
    verify(friendshipRepository, times(1)).flush();
    verify(notificationService, times(1))
            .createUserNotification(eq(testUser1), anyString());
    verify(notificationService, times(1))
            .createUserNotification(eq(testUser2), anyString());
  }

  @Test
  public void testAcceptRequest_nonExistentFriendship_throwsError() {
    // when
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(null);
    // then
    assertThrows(ResponseStatusException.class, () -> friendshipService.acceptRequest(testUser2, testUser1));
  }

  @Test
  public void testDeleteFriend_success() {
    // when
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
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
  public void testDeleteFriend_1_success() {
    // when
    Mockito.when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
    friendshipService.deleteFriend(testUser2, testUser1);

    // then
    verify(friendshipRepository, times(1)).delete(any(Friendship.class));
    verify(notificationService, times(1)).createUserNotification(eq(testUser1), anyString());
    verify(notificationService, times(1)).createUserNotification(eq(testUser2), anyString());
  }

  @Test
  public void testDeleteFriend_2_success() {
    // when
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
    friendshipService.deleteFriend(testUser1, testUser2);

    // then
    verify(friendshipRepository, times(1)).delete(any(Friendship.class));
    verify(notificationService, times(1)).createUserNotification(eq(testUser1), anyString());
    verify(notificationService, times(1)).createUserNotification(eq(testUser2), anyString());
  }


  @Test
  public void testDeleteFriend_conflict_throwsConflictError() {
    // then
    assertThrows(ResponseStatusException.class, () -> friendshipService.deleteFriend(testUser2, testUser1));
  }


  @Test
  public void testGetAllReceivedFriendRequests_success() {
    // when
    List<Friendship> fr = new ArrayList<>();
    fr.add(testFriendship);
    when(friendshipRepository.findAllByFriend2AndStatus(testUser2, FriendShipStatus.PENDING)).thenReturn(fr);
    List<Friend> friends = friendshipService.getAllReceivedFriendRequests(testUser2);

    // then
    assertEquals(1, friends.size());
    assertEquals(testUser1.getId(), friends.get(0).getFriendId());
    assertEquals(0, friends.get(0).getPoints());
    assertEquals(FriendShipStatus.PENDING, friends.get(0).getStatus());
    assertEquals(testUser1.getUsername(), friends.get(0).getUsername());
  }

  @Test
  public void testGetAllSentFriendRequests_success() {
    // when
    List<Friendship> fr = new ArrayList<>();
    fr.add(testFriendship);
    when(friendshipRepository.findAllByFriend1AndStatus(testUser1, FriendShipStatus.PENDING)).thenReturn(fr);
    List<Friend> friends = friendshipService.getAllSentFriendRequests(testUser1);

    // then
    assertEquals(1, friends.size());
    assertEquals(testUser2.getId(), friends.get(0).getFriendId());
    assertEquals(0, friends.get(0).getPoints());
    assertEquals(FriendShipStatus.PENDING, friends.get(0).getStatus());
    assertEquals(testUser2.getUsername(), friends.get(0).getUsername());
  }



  @Test
  public void testFindFriendStatusSearch_Incomplete_success() {
    // when
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
    FriendshipStatusSearch friendshipStatusSearch1 = friendshipService.findFriendStatusSearch(testUser1, testUser2);
    FriendshipStatusSearch friendshipStatusSearch2 = friendshipService.findFriendStatusSearch(testUser2, testUser1);

    // then
    assertEquals(FriendshipStatusSearch.SENT, friendshipStatusSearch1);
    assertEquals(FriendshipStatusSearch.RECEIVED, friendshipStatusSearch2);
  }

  @Test
  public void testFindFriendStatusSearch_NonExistent_success() {
    // when
    FriendshipStatusSearch friendshipStatusSearch1 = friendshipService.findFriendStatusSearch(testUser1, testUser3);
    FriendshipStatusSearch friendshipStatusSearch2 = friendshipService.findFriendStatusSearch(testUser3, testUser1);

    // then
    assertEquals(FriendshipStatusSearch.NOTHING, friendshipStatusSearch1);
    assertEquals(FriendshipStatusSearch.NOTHING, friendshipStatusSearch2);
  }

  @Test
  public void testIncreasePoints_success() {
    // given
    when(friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2)).thenReturn(testFriendship);
    List<User> users = new ArrayList<>();
    users.add(testUser1);
    users.add(testUser2);

    // when
    friendshipService.increasePoints(users);

    // then
    verify(friendshipRepository, times(1)).findByFriend1AndFriend2(testUser1, testUser2);
    verify(friendshipRepository, times(1)).save(any(Friendship.class));
  }


}
