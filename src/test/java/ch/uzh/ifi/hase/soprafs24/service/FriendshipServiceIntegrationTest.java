package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.FriendshipRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
public class FriendshipServiceIntegrationTest {

  @Autowired
  private FriendshipService friendshipService;

  @Qualifier("friendshipRepository")
  @Autowired
  private FriendshipRepository friendshipRepository;

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @MockBean
  private NotificationService notificationService;

  private User testUser1;
  private User testUser2;
  private User testUser3;

  @BeforeEach
  public void setup() {
    // Clear any existing data in the repositories
    friendshipRepository.deleteAll();
    friendshipRepository.flush();

    // Create test users
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

    testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setUsername("user2");
    testUser2.setPassword("Firstname Lastname");
    testUser2.setStatus(UserStatus.ONLINE);
    testUser2.setToken("123");
    testUser2.setCreationDate(LocalDate.of(2020,11,11));
    testUser2.setBirthday(LocalDate.of(2020,11,11));
    testUser2.setEmail("firstname.lastname@something.com");
    testUser2.setLevel(1.00);

    testUser3 = new User();
    testUser3.setId(3L);
    testUser3.setUsername("user3");
    testUser3.setPassword("Firstname Lastname");
    testUser3.setStatus(UserStatus.ONLINE);
    testUser3.setToken("xyz");
    testUser3.setCreationDate(LocalDate.of(2020,11,11));
    testUser3.setBirthday(LocalDate.of(2020,11,11));
    testUser3.setEmail("firstname.lastname@something.com");
    testUser3.setLevel(1.00);

    testUser1 = userRepository.save(testUser1);
    testUser2 = userRepository.save(testUser2);
    testUser3 = userRepository.save(testUser3);
    userRepository.flush();
  }

  @Test
  public void testSendRequest_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Verify that the friendship has been created and is pending
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);

    assertNotNull(friendship);
    assertEquals(FriendShipStatus.PENDING, friendship.getStatus());
    assertEquals(testUser1.getId(), friendship.getFriend1().getId());
    assertEquals(testUser2.getId(), friendship.getFriend2().getId());
    assertEquals(0, friendship.getPoints());
  }

  @Test
  public void testAcceptRequest_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Accept the friend request
    friendshipService.acceptRequest(testUser2, testUser1);

    // Verify that the friendship status has changed to accepted
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);
    assertEquals(FriendShipStatus.ACCEPTED, friendship.getStatus());
    assertEquals(testUser1.getId(), friendship.getFriend1().getId());
    assertEquals(testUser2.getId(), friendship.getFriend2().getId());
    assertEquals(0, friendship.getPoints());
  }

  @Test
  public void testDeleteFriend_1_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Reject the friendship
    friendshipService.deleteFriend(testUser2, testUser1);

    // Verify that the friendship has been deleted
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);
    assertNull(friendship);
  }
  @Test
  public void testDeleteFriend_2_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Withdraw the friendship
    friendshipService.deleteFriend(testUser1, testUser2);

    // Verify that the friendship has been deleted
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);
    assertNull(friendship);
  }
  @Test
  public void testDeleteFriend_3_success() {
    // Send a friend request and accept it
    friendshipService.sendRequest(testUser1, testUser2);
    friendshipService.acceptRequest(testUser2, testUser1);

    // Delete the friendship
    friendshipService.deleteFriend(testUser2, testUser1);

    // Verify that the friendship has been deleted
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);
    assertNull(friendship);
  }
  @Test
  public void testDeleteFriend_4_success() {
    // Send a friend request and accept it
    friendshipService.sendRequest(testUser1, testUser2);
    friendshipService.acceptRequest(testUser2, testUser1);

    // Delete the friendship
    friendshipService.deleteFriend(testUser1, testUser2);

    // Verify that the friendship has been deleted
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);
    assertNull(friendship);
  }

  @Test
  public void testSendRequest_toYourself_throwsError() {
    // Verify that sending a friend request to yourself throws an error
    assertThrows(ResponseStatusException.class, () -> friendshipService.sendRequest(testUser1, testUser1));
  }

  @Test
  public void testSendRequest_duplicate_throwsError() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Verify that sending a duplicate friend request throws an error
    assertThrows(ResponseStatusException.class, () -> friendshipService.sendRequest(testUser1, testUser2));
  }

  @Test
  public void testAcceptRequest_nonExistentFriendship_throwsError() {
    // Verify that accepting a non-existent friendship throws an error
    assertThrows(ResponseStatusException.class, () -> friendshipService.acceptRequest(testUser2, testUser1));
  }

  @Test
  public void testDeleteFriend_noFriendship_throwsNotFoundError() {
    // Verify that deleting a non-existent friendship throws a not found error
    assertThrows(ResponseStatusException.class, () -> friendshipService.deleteFriend(testUser1, testUser2));
  }

  @Test
  public void testDeleteAllForAUser_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Delete all friendships of testUser1
    friendshipService.deleteAllForAUser(testUser1);

    // Verify that the friendship has been deleted
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);
    assertNull(friendship);
  }

  @Test
  public void testGetAllReceivedFriendRequests_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Get all received friend requests of testUser2
    List<Friend> friends = friendshipService.getAllReceivedFriendRequests(testUser2);

    // Verify
    assertEquals(1, friends.size());
    assertEquals(testUser1.getId(), friends.get(0).getFriendId());
    assertEquals(0, friends.get(0).getPoints());
    assertEquals(FriendShipStatus.PENDING, friends.get(0).getStatus());
    assertEquals(testUser1.getUsername(), friends.get(0).getUsername());
  }

  @Test
  public void testGetAllSentFriendRequests_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Get all sent friend requests of testUser1
    List<Friend> friends = friendshipService.getAllSentFriendRequests(testUser1);

    // Verify
    assertEquals(1, friends.size());
    assertEquals(testUser2.getId(), friends.get(0).getFriendId());
    assertEquals(0, friends.get(0).getPoints());
    assertEquals(FriendShipStatus.PENDING, friends.get(0).getStatus());
    assertEquals(testUser2.getUsername(), friends.get(0).getUsername());
  }

  @Test
  public void testGetAllAcceptedFriends_success() {
    // Send two friend requests and accept both
    friendshipService.sendRequest(testUser1, testUser2);
    friendshipService.acceptRequest(testUser2, testUser1);
    friendshipService.sendRequest(testUser3, testUser1);
    friendshipService.acceptRequest(testUser1, testUser3);

    // Get all accepted friend requests of testUser1
    List<Friend> friends = friendshipService.getAllAcceptedFriends(testUser1);

    // Verify
    assertEquals(2, friends.size());
    assertEquals(testUser2.getId(), friends.get(0).getFriendId());
    assertEquals(0, friends.get(0).getPoints());
    assertEquals(FriendShipStatus.ACCEPTED, friends.get(0).getStatus());
    assertEquals(testUser2.getUsername(), friends.get(0).getUsername());
    assertEquals(testUser3.getId(), friends.get(1).getFriendId());
    assertEquals(0, friends.get(1).getPoints());
    assertEquals(FriendShipStatus.ACCEPTED, friends.get(1).getStatus());
    assertEquals(testUser3.getUsername(), friends.get(1).getUsername());
  }

  @Test
  public void testFindFriendStatusSearch_Completed_success() {
    // Send a friend request and accept it
    friendshipService.sendRequest(testUser1, testUser2);
    friendshipService.acceptRequest(testUser2, testUser1);

    // Get status
    FriendshipStatusSearch friendshipStatusSearch1 = friendshipService.findFriendStatusSearch(testUser1, testUser2);
    FriendshipStatusSearch friendshipStatusSearch2 = friendshipService.findFriendStatusSearch(testUser2, testUser1);

    // Verify
    assertEquals(FriendshipStatusSearch.COMPLETED, friendshipStatusSearch1);
    assertEquals(FriendshipStatusSearch.COMPLETED, friendshipStatusSearch2);
  }

  @Test
  public void testFindFriendStatusSearch_Incomplete_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Get status
    FriendshipStatusSearch friendshipStatusSearch1 = friendshipService.findFriendStatusSearch(testUser1, testUser2);
    FriendshipStatusSearch friendshipStatusSearch2 = friendshipService.findFriendStatusSearch(testUser2, testUser1);

    // Verify
    assertEquals(FriendshipStatusSearch.SENT, friendshipStatusSearch1);
    assertEquals(FriendshipStatusSearch.RECEIVED, friendshipStatusSearch2);
  }

  @Test
  public void testFindFriendStatusSearch_NonExistent_success() {
    // Get status
    FriendshipStatusSearch friendshipStatusSearch1 = friendshipService.findFriendStatusSearch(testUser1, testUser2);
    FriendshipStatusSearch friendshipStatusSearch2 = friendshipService.findFriendStatusSearch(testUser2, testUser1);

    // Verify
    assertEquals(FriendshipStatusSearch.NOTHING, friendshipStatusSearch1);
    assertEquals(FriendshipStatusSearch.NOTHING, friendshipStatusSearch2);
  }

  @Test
  public void testIncreasePoints_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);
    friendshipService.acceptRequest(testUser2, testUser1);

    // Increase and get friendship
    List<User> users = new ArrayList<>();
    users.add(testUser1);
    users.add(testUser2);
    friendshipService.increasePoints(users);
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(testUser1, testUser2);

    // Verify
    assertEquals(60, friendship.getPoints());
  }

  @Test
  public void testGetAllAcceptedFriendsAsUsers_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);
    friendshipService.acceptRequest(testUser2, testUser1);

    // Get friends as users
    List<User> users = friendshipService.getAllAcceptedFriendsAsUsers(testUser1);

    // Verify
    assertEquals(1, users.size());
    assertEquals(testUser2.getId(), users.get(0).getId());
  }

}