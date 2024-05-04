package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.TemplatePackingItem;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.FeedbackRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TemplatePackingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
* Test class for the UserResource REST resource.
*
* @see UserService
*/
@WebAppConfiguration
@SpringBootTest
@Transactional
@Rollback
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Qualifier("templatePackingRepository")
  @Autowired
  private TemplatePackingRepository templatePackingRepository;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private UserService userService;

  private User testUser1;

  private TemplatePackingItem testItem;
  private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @BeforeEach
  void setup() {
    // Clear any existing data in the repositories
    userRepository.deleteAll();
    userRepository.flush();
    templatePackingRepository.deleteAll();
    templatePackingRepository.flush();

    // Create test user
    testUser1 = new User();
    testUser1.setId(1L);
    testUser1.setUsername("user1");
    testUser1.setPassword("Firstname Lastname");
    String encodedPassword = passwordEncoder.encode("Firstname Lastname");
    testUser1.setPassword(encodedPassword);
    testUser1.setStatus(UserStatus.ONLINE);
    testUser1.setToken("abc");
    testUser1.setCreationDate(LocalDate.of(2020, 11, 11));
    testUser1.setBirthday(LocalDate.of(2020, 11, 11));
    testUser1.setEmail("firstname.lastname@something.com");
    testUser1.setLevel(1.00);
    testUser1.setLastOnline(LocalDateTime.of(2030, 11, 11, 11, 11));

    testUser1 = userRepository.save(testUser1);
    userRepository.flush();

    // Create test item
    testItem = new TemplatePackingItem();
    testItem.setItem("test item");
    testItem.setUser(testUser1);
    testItem.setId(1L);

    testItem = templatePackingRepository.save(testItem);
    templatePackingRepository.flush();

  }


  @Test
  void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }

  @Test
  void createUser_duplicateUsername_throwsException() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setPassword("testName2");
    testUser2.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }

  @Test
  void getUserByToken_tokenExists_success() {
    // when
    User foundUser = userService.getUserByToken(testUser1.getToken());

    // then
    assertEquals(testUser1.getId(), foundUser.getId());
    assertEquals(testUser1.getUsername(), foundUser.getUsername());
    assertNotNull(foundUser.getToken());
    assertEquals(UserStatus.ONLINE, foundUser.getStatus());

  }

  @Test
  void getUserByToken_tokenNonExistant_throwsException() {

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.getUserByToken("ab"));
  }

  @Test
  void getUserById_idExists_success() {
    // when
    User foundUser = userService.getUserById(testUser1.getId());

    // then
    assertEquals(testUser1.getId(), foundUser.getId());
    assertEquals(testUser1.getUsername(), foundUser.getUsername());
    assertNotNull(foundUser.getToken());
    assertEquals(UserStatus.ONLINE, foundUser.getStatus());
  }

  @Test
  void getUserById_idNonExistant_throwsException() {
    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.getUserById(2L));
  }

  @Test
  void loginUser_userExists_success() {
    // when
    User testUser = new User();
    testUser.setUsername("user1");
    testUser.setPassword("Firstname Lastname");

    String token = userService.loginUser(testUser);

    // then
    assertEquals(testUser1.getToken(), token);
  }

  @Test
  void loginUser_userNonExistant_throwsException() {
    // when
    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));
    testUser.setToken("ab");

    assertThrows(ResponseStatusException.class, () -> userService.loginUser(testUser));
  }

  @Test
  void updateUser_userExists_success() {
    // when
    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));
    userService.updateUser("abc", testUser);

    // then
    assertEquals(testUser1.getUsername(), testUser.getUsername());
    assertTrue(passwordEncoder.matches(testUser.getPassword(), testUser1.getPassword()));
    assertEquals(testUser1.getEmail(), testUser.getEmail());
    assertEquals(testUser1.getBirthday(), testUser.getBirthday());
    assertNotNull(testUser1.getToken());
    assertEquals(UserStatus.ONLINE, testUser1.getStatus());
  }

  @Test
  void updateUser_userNonExistant_throwsException() {
    // when
    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));

    // then
    assertThrows(ResponseStatusException.class, () -> userService.updateUser("ab", testUser));
  }

  @Test
  void deleteUser_userExists_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));

    // when
    User createdUser = userService.createUser(testUser);

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());

    userService.deleteUser(createdUser.getToken());

    // then
    assertNull(userRepository.findByUsername("testUsername"));
  }

  @Test
  void deleteUser_userNonExistant_throwsException() {
    // then
    assertThrows(ResponseStatusException.class, () -> userService.deleteUser("ab"));
  }

  @Test
  void logoutUser_userExists_success() {
    userService.logoutUser("abc");

    // then
    assertEquals(UserStatus.OFFLINE, testUser1.getStatus());
  }

  @Test
  void logoutUser_userNonExistant_throwsException() {
    // then
    assertThrows(ResponseStatusException.class, () -> userService.logoutUser("ab"));
  }

  @Test
  void getMatchingUsers_NameExists_success() {

    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));

    // when
    User createdUser = userService.createUser(testUser);

    List<User> matchingUsers = userService.getMatchingUsers("abc", "test");

    assertEquals(matchingUsers.get(0).getId(), createdUser.getId());
    assertEquals(matchingUsers.get(0).getUsername(), createdUser.getUsername());
    assertEquals(matchingUsers.get(0).getEmail(), createdUser.getEmail());
    assertNotNull(matchingUsers.get(0).getToken());
  }

  @Test
  void getMatchingUsers_NameNonExistent_emptyList() {

    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("testUser@gmail.com");
    testUser.setBirthday(LocalDate.of(2024, 11, 11));

    // when
    User createdUser = userService.createUser(testUser);

    List<User> matchingUsers = userService.getMatchingUsers("abc", "xyz");

    assertEquals(0, matchingUsers.size());
    assertNotNull(matchingUsers);
  }

  @Test
  void giveFeedback_success() {
    userService.giveFeedback(testUser1, "this is a test feedback");

    assertEquals("Thank you for giving us feedback, we are happy to look at it!", notificationService.getUserNotifications(testUser1).get(0).getMessage());
  }

  @Test
  void increaseLevel_levelIncreased_success() {
    userService.increaseLevel(testUser1, 5.0D);

    assertEquals(6.0D, testUser1.getLevel());
  }
  @Test
  void getItems_itemsReturned_success() {

    List<TemplatePackingItem> returnedItems = userService.getItems(testUser1);

    assertEquals(returnedItems.get(0).getItem(), testItem.getItem());
    assertEquals(returnedItems.get(0).getUser(), testItem.getUser());
    assertEquals(returnedItems.get(0).getId(), testItem.getId());
  }

  @Test
  void getItem_itemReturned_success() {

    TemplatePackingItem savedItem = userService.addItem(testUser1, testItem);

    TemplatePackingItem returnedItem = userService.getItem(testItem.getId());

    assertEquals(returnedItem.getItem(), testItem.getItem());
    assertEquals(returnedItem.getUser(), testItem.getUser());
    assertEquals(returnedItem.getId(), testItem.getId());
  }

  @Test
  void getItem_itemNonExistant_throwsException() {

    assertThrows(ResponseStatusException.class, () -> userService.getItem(2L));
  }

  @Test
  void addItem_itemAdded_success() {
    TemplatePackingItem templatePackingItem = new TemplatePackingItem();
    templatePackingItem.setItem("test item");
    templatePackingItem.setUser(testUser1);
    templatePackingItem.setId(1L);

    TemplatePackingItem savedItem = userService.addItem(testUser1, templatePackingItem);

    assertEquals(templatePackingItem.getItem(), savedItem.getItem());
    assertEquals(templatePackingItem.getUser(), savedItem.getUser());
    assertEquals(templatePackingItem.getId(), savedItem.getId());
  }

  @Test
  void deleteItem_itemDeleted_success() {
    TemplatePackingItem templatePackingItem = new TemplatePackingItem();
    templatePackingItem.setItem("test item");
    templatePackingItem.setUser(testUser1);
    templatePackingItem.setId(1L);

    TemplatePackingItem savedItem = userService.addItem(testUser1, templatePackingItem);

    assertEquals(templatePackingItem.getItem(), savedItem.getItem());
    assertEquals(templatePackingItem.getUser(), savedItem.getUser());
    assertEquals(templatePackingItem.getId(), savedItem.getId());

    userService.deleteItem(testUser1, 1L);

    assertThrows(ResponseStatusException.class, () -> userService.getItem(1L));
  }

  @Test
  void updateItem_itemUpdated_success() {
    TemplatePackingItem updatedItem = new TemplatePackingItem();
    updatedItem.setItem("test item");
    updatedItem.setUser(testUser1);

    userService.updateItem(testUser1, 1L, updatedItem);

    assertEquals(testItem.getItem(), updatedItem.getItem());
    assertEquals(testItem.getUser(), updatedItem.getUser());
  }
}
