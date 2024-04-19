package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class FriendshipRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private FriendshipRepository friendshipRepository;

  private User createUserDummy(String un, String to) {
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername(un);
    user.setStatus(UserStatus.ONLINE);
    user.setToken(to);
    user.setCreationDate(LocalDate.of(2020, 11, 11));
    user.setBirthday(LocalDate.of(2020, 11, 11));
    user.setEmail("firstname.lastname@something.com");
    user.setLevel(1.00);
    user.setLastOnline(LocalDateTime.of(2030,11,11,11,11));
    return user;
  }

  @Test
  public void findAllByFriend1_success() {
    // given
    User user1 = createUserDummy("user1", "abc");
    User user2 = createUserDummy("user2", "123");
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.flush();

    Friendship friendship1 = new Friendship();
    friendship1.setFriend1(user1);
    friendship1.setFriend2(user2);
    friendship1.setStatus(FriendShipStatus.ACCEPTED);
    friendship1.setPoints(0);
    entityManager.persist(friendship1);
    entityManager.flush();

    // when
    List<Friendship> friendships = friendshipRepository.findAllByFriend1(user1);

    // then
    assertNotNull(friendships);
    assertEquals(friendships.size(), 1);
    assertTrue(friendships.contains(friendship1));
  }

  @Test
  public void findAllByFriend2_success() {
    // given
    User user1 = createUserDummy("user1", "abc");
    User user2 = createUserDummy("user2", "123");
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.flush();

    Friendship friendship1 = new Friendship();
    friendship1.setFriend1(user1);
    friendship1.setFriend2(user2);
    friendship1.setStatus(FriendShipStatus.ACCEPTED);
    friendship1.setPoints(0);
    entityManager.persist(friendship1);
    entityManager.flush();

    // when
    List<Friendship> friendships = friendshipRepository.findAllByFriend2(user2);

    // then
    assertNotNull(friendships);
    assertEquals(friendships.size(), 1);
    assertTrue(friendships.contains(friendship1));
  }

  @Test
  public void findByFriend1AndFriend2_success() {
    // given
    User user1 = createUserDummy("user1", "abc");
    User user2 = createUserDummy("user2", "123");
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.flush();

    Friendship friendship1 = new Friendship();
    friendship1.setFriend1(user1);
    friendship1.setFriend2(user2);
    friendship1.setStatus(FriendShipStatus.ACCEPTED);
    friendship1.setPoints(0);
    entityManager.persist(friendship1);
    entityManager.flush();

    // when
    Friendship foundFriendship = friendshipRepository.findByFriend1AndFriend2(user1, user2);

    // then
    assertNotNull(foundFriendship);
    assertEquals(foundFriendship, friendship1);
  }

  @Test
  public void findAllByFriend1AndStatus_success() {
    // given
    User user1 = createUserDummy("user1", "abc");
    User user2 = createUserDummy("user2", "123");
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.flush();

    Friendship friendship1 = new Friendship();
    friendship1.setFriend1(user1);
    friendship1.setFriend2(user2);
    friendship1.setStatus(FriendShipStatus.ACCEPTED);
    friendship1.setPoints(0);
    entityManager.persist(friendship1);
    entityManager.flush();

    // when
    List<Friendship> friendships = friendshipRepository.findAllByFriend1AndStatus(user1, FriendShipStatus.ACCEPTED);

    // then
    assertNotNull(friendships);
    assertEquals(friendships.size(), 1);
    assertTrue(friendships.contains(friendship1));
  }

  @Test
  public void findAllByFriend2AndStatus_success() {
    // given
    User user1 = createUserDummy("user1", "abc");
    User user2 = createUserDummy("user2", "123");
    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.flush();

    Friendship friendship1 = new Friendship();
    friendship1.setFriend1(user1);
    friendship1.setFriend2(user2);
    friendship1.setStatus(FriendShipStatus.ACCEPTED);
    friendship1.setPoints(0);
    entityManager.persist(friendship1);
    entityManager.flush();

    // when
    List<Friendship> friendships = friendshipRepository.findAllByFriend2AndStatus(user2, FriendShipStatus.ACCEPTED);

    // then
    assertNotNull(friendships);
    assertEquals(friendships.size(), 1);
    assertTrue(friendships.contains(friendship1));
  }
}
