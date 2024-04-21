package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserNotification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserNotificationRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserNotificationRepository userNotificationRepository;

  private User createUserDummy() {
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.ONLINE);
    user.setToken("123abc");
    user.setCreationDate(LocalDate.of(2020,11,11));
    user.setBirthday(LocalDate.of(2020,11,11));
    user.setEmail("firstname.lastname@something.com");
    user.setLevel(1.00);
    user.setLastOnline(LocalDateTime.of(2030,11,11,11,11));
    return user;
  }
  @Test
  void findAllByUser_success() {
    // given
    User user = createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    UserNotification un = new UserNotification();
    un.setMessage("This is the first message");
    un.setTimeStamp(LocalDateTime.of(2024,11,11,11,11));
    un.setUser(user);

    entityManager.persist(un);
    entityManager.flush();

    // when
    List<UserNotification> found = userNotificationRepository.findAllByUser(user);

    // then
    assertEquals(found.size(), 1);
    assertNotNull(found.get(0).getId());
    assertEquals(un.getMessage(), found.get(0).getMessage());
    assertEquals(un.getTimeStamp(), found.get(0).getTimeStamp());
    assertEquals(un.getUser(), found.get(0).getUser());
  }

  @Test
  void deleteAllByUser_success() {
    // given
    User user = createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    UserNotification un1 = new UserNotification();
    un1.setMessage("This is the first message");
    un1.setTimeStamp(LocalDateTime.of(2024, 11, 11, 11, 11));
    un1.setUser(user);

    UserNotification un2 = new UserNotification();
    un2.setMessage("This is the second message");
    un2.setTimeStamp(LocalDateTime.of(2024, 11, 12, 11, 11));
    un2.setUser(user);

    entityManager.persist(un1);
    entityManager.persist(un2);
    entityManager.flush();

    // when
    userNotificationRepository.deleteAllByUser(user);

    // then
    List<UserNotification> found = userNotificationRepository.findAllByUser(user);
    assertEquals(0, found.size());
  }
}
