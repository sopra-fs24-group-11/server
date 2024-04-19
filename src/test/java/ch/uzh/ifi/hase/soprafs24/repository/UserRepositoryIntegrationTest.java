package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
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
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  private User createUserDummy(String un, String to) {
    User user = new User();
    user.setUsername(un);
    user.setPassword("Firstname Lastname");
    user.setStatus(UserStatus.ONLINE);
    user.setToken(to);
    user.setCreationDate(LocalDate.of(2020,11,11));
    user.setBirthday(LocalDate.of(2020,11,11));
    user.setEmail("firstname.lastname@something.com");
    user.setLevel(1.00);
    user.setLastOnline(LocalDateTime.of(2030,11,11,11,11));
    return user;
  }

  @Test
  public void findByUsername_success() {
    // given
    User user = createUserDummy("firstname@lastname", "123abc");

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByUsername(user.getUsername());

    // then
    assertNotNull(found.getId());
    assertEquals(found.getPassword(), user.getPassword());
    assertEquals(found.getUsername(), user.getUsername());
    assertEquals(found.getStatus(), user.getStatus());
    assertEquals(found.getToken(), user.getToken());
    assertEquals(found.getCreationDate(), user.getCreationDate());
    assertEquals(found.getBirthday(), user.getBirthday());
    assertEquals(found.getEmail(), user.getEmail());
    assertEquals(found.getLevel(), user.getLevel());
  }

  @Test
  public void findByToken_success() {
    // given
    User user = createUserDummy("firstname@lastname", "123abc");

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByToken(user.getToken());

    // then
    assertNotNull(found.getId());
    assertEquals(found.getPassword(), user.getPassword());
    assertEquals(found.getUsername(), user.getUsername());
    assertEquals(found.getStatus(), user.getStatus());
    assertEquals(found.getToken(), user.getToken());
    assertEquals(found.getCreationDate(), user.getCreationDate());
    assertEquals(found.getBirthday(), user.getBirthday());
    assertEquals(found.getEmail(), user.getEmail());
    assertEquals(found.getLevel(), user.getLevel());
  }

  @Test
  public void findAllByUsernameStartsWith_success() {
    // given
    User user1 = createUserDummy("found1", "123");
    User user2 = createUserDummy("found2", "234");;
    User user3 = createUserDummy("404", "345");

    entityManager.persist(user1);
    entityManager.persist(user2);
    entityManager.persist(user3);
    entityManager.flush();

    // when
    List<User> found = userRepository.findAllByUsernameStartsWith("fo");

    // then
    assertEquals(found.size(), 2);
    assertTrue(found.contains(user1)); // User user1 should be found
    assertTrue(found.contains(user2)); // User user2 should be found
    assertFalse(found.contains(user3)); // User user3 should not be found
  }

}
