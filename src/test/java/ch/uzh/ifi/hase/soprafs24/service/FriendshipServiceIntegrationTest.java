package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    userRepository.save(testUser1);
    userRepository.save(testUser2);
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
  public void testDeleteFriend_success() {
    // Send a friend request
    friendshipService.sendRequest(testUser1, testUser2);

    // Delete the friendship
    friendshipService.deleteFriend(testUser2, testUser1);

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

}