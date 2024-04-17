package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.TemplatePackingItem;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class TemplatePackingRepositoryIntegrationTest {
  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private TemplatePackingRepository templatePackingRepository;

  private User createUserDummy() {
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.ONLINE);
    user.setToken("abc123");
    user.setCreationDate(LocalDate.of(2020,11,11));
    user.setBirthday(LocalDate.of(2020,11,11));
    user.setEmail("firstname.lastname@something.com");
    user.setLevel(1.00);
    return user;
  }

  @Test
  public void findByIdAndUser_success() {
// given
    User user = createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    TemplatePackingItem item = new TemplatePackingItem();
    item.setUser(user);
    item.setItem("Toothbrush");

    entityManager.persist(item);
    entityManager.flush();

    // when
    TemplatePackingItem found = templatePackingRepository.findByIdAndUser(item.getId(), user);

    // then
    assertNotNull(found);
    assertNotNull(found.getId());
    assertEquals(found.getUser(), user);
    assertEquals(found.getItem(), item.getItem());
  }

  @Test
  public void deleteAllByUser_success() {
// given
    User user = createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    TemplatePackingItem item1 = new TemplatePackingItem();
    item1.setUser(user);
    item1.setItem("Toothbrush");

    TemplatePackingItem item2 = new TemplatePackingItem();
    item2.setUser(user);
    item2.setItem("Pyjamas");

    entityManager.persist(item1);
    entityManager.persist(item2);
    entityManager.flush();

    // when
    templatePackingRepository.deleteAllByUser(user);

    // then
    List<TemplatePackingItem> found = templatePackingRepository.findAllByUser(user);
    assertEquals(found.size(), 0);
  }

  @Test
  public void findAllByUser_success() {
// given
    User user = createUserDummy();
    entityManager.persist(user);
    entityManager.flush();

    TemplatePackingItem item1 = new TemplatePackingItem();
    item1.setUser(user);
    item1.setItem("Toothbrush");

    TemplatePackingItem item2 = new TemplatePackingItem();
    item2.setUser(user);
    item2.setItem("Pyjamas");

    entityManager.persist(item1);
    entityManager.persist(item2);
    entityManager.flush();

    // when
    List<TemplatePackingItem> found = templatePackingRepository.findAllByUser(user);

    // then
    assertEquals(found.size(), 2);
    assertTrue(found.contains(item1));
    assertTrue(found.contains(item2));
  }
}