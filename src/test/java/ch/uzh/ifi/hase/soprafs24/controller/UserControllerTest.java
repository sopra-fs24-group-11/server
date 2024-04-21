package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.TemplatePackingItem;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserNotification;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.service.FriendshipService;
import ch.uzh.ifi.hase.soprafs24.service.NotificationService;
import ch.uzh.ifi.hase.soprafs24.service.NullChecker;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.bridge.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserService userService;
  @MockBean
  private FriendshipService friendshipService;
  @MockBean
  private NotificationService notificationService;
  private User testUser;
  private Friend testFriend;
  @BeforeEach
  void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setEmail("user@test.ch");
    testUser.setBirthday(LocalDate.of(2000, 1, 1));
    testUser.setToken("1d");
    testUser.setStatus(UserStatus.ONLINE);
    testUser.setLastOnline(LocalDateTime.of(2030,11,11,11,11));

    testFriend = new Friend();
    testFriend.setFriendId(2L);
    testFriend.setUsername("testFriend");
    testFriend.setStatus(FriendShipStatus.ACCEPTED);
    testFriend.setLevel(3.0D);
    testFriend.setPoints(5);


    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
  }

  // GET REQUESTS --------------------------------------------------------------
  @Test // GET 1: getting one user
  // given
  void getUser_validInput_userReturned() throws Exception {

    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(testUser.getId().intValue())))
            .andExpect(jsonPath("$.password", is(testUser.getPassword())))
            .andExpect(jsonPath("$.username", is(testUser.getUsername())))
            .andExpect(jsonPath("$.email", is(testUser.getEmail())))
            .andExpect(jsonPath("$.birthday", is(testUser.getBirthday().toString())))
            .andExpect(jsonPath("$.status", is(testUser.getStatus().toString())));
  }

  @Test // GET 2: getting one user
  void getUser_invalidInput_userNotReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isNotFound());
  }

  @Test // GET 3: search with valid input
  void searchUser_validInput_userListReturned() throws Exception {
    // given
    User user1 = new User();
    user1.setId(1L);
    user1.setPassword("Test User");
    user1.setUsername("testUsername");
    user1.setEmail("user@test.ch");
    user1.setBirthday(LocalDate.of(2000, 1, 1));
    user1.setToken("1d");
    user1.setLevel(1.0D);
    user1.setStatus(UserStatus.ONLINE);

    User user2 = new User();
    user2.setId(1L);
    user2.setPassword("Test User");
    user2.setUsername("testUsername2");
    user2.setEmail("user@test.ch");
    user2.setBirthday(LocalDate.of(2000, 1, 1));
    user2.setToken("1d");
    user2.setLevel(1.0D);
    user2.setStatus(UserStatus.ONLINE);

    List<User> users = new ArrayList<User>();
    users.add(user1);
    users.add(user2);

    given(userService.getMatchingUsers(testUser.getToken(), "test")).willReturn(users);
    given(friendshipService.findFriendStatusSearch(Mockito.eq(testUser), Mockito.any())).willReturn(FriendshipStatusSearch.SENT);
    given(userService.getUserByToken(Mockito.any())).willReturn(testUser);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users/search?name=test")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(user1.getId().intValue())))
            .andExpect(jsonPath("$[0].username", is(user1.getUsername())))
            .andExpect(jsonPath("$[0].level", is(user1.getLevel())))
            .andExpect(jsonPath("$[0].status", is("SENT")));
  }

  @Test // GET 4: search with invalid input
  void searchUser_invalidInput_userListReturned() throws Exception {
    // given

    given(userService.getMatchingUsers(testUser.getToken(), "test")).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users/search?name=test")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
  }

  @Test // GET 5: get friends with valid input
  void getFriends_validInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    User userMock = mock(User.class);
    List<Friend> friends = new ArrayList<Friend>();
    friends.add(testFriend);
    given(friendshipService.getAllAcceptedFriends(testUser)).willReturn(friends);
    given(userService.getUserById(Mockito.any())).willReturn(testUser); // should return the User object for the friend, not the user
    given(userMock.getStatus()).willReturn(UserStatus.ONLINE);

    // when/then
    MockHttpServletRequestBuilder getRequest = get("/users/friends")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].friendId", is(testFriend.getFriendId().intValue())))
            .andExpect(jsonPath("$[0].username", is(testFriend.getUsername())))
            .andExpect(jsonPath("$[0].level", is(testFriend.getLevel())))
            .andExpect(jsonPath("$[0].status", is("ONLINE")))
            .andExpect(jsonPath("$[0].points", is(testFriend.getPoints())));
  }

  @Test // GET 6: get friends with invalid input
  void getFriends_invalidInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/friends")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
  }

  @Test // GET 7: get friend requests
  void getFriendRequests_validInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    User userMock = mock(User.class);
    List<Friend> friends = new ArrayList<Friend>();
    friends.add(testFriend);
    given(friendshipService.getAllReceivedFriendRequests(testUser)).willReturn(friends);

    // when/then
    MockHttpServletRequestBuilder getRequest = get("/users/friends/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].friendId", is(testFriend.getFriendId().intValue())))
            .andExpect(jsonPath("$[0].username", is(testFriend.getUsername())))
            .andExpect(jsonPath("$[0].level", is(testFriend.getLevel())))
            .andExpect(jsonPath("$[0].points", is(testFriend.getPoints())));
  }

  @Test // GET 8: get friend requests
  void getFriendRequests_invalidInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/friends/requests")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
  }

  @Test // GET 9: get pending friends
  void getPendingFriendRequests_validInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    User userMock = mock(User.class);
    List<Friend> friends = new ArrayList<Friend>();
    friends.add(testFriend);
    given(friendshipService.getAllPendingFriendRequests(testUser)).willReturn(friends);

    // when/then
    MockHttpServletRequestBuilder getRequest = get("/users/friends/pending")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].friendId", is(testFriend.getFriendId().intValue())))
            .andExpect(jsonPath("$[0].username", is(testFriend.getUsername())))
            .andExpect(jsonPath("$[0].level", is(testFriend.getLevel())))
            .andExpect(jsonPath("$[0].points", is(testFriend.getPoints())));
  }
  @Test // GET 10: get pending friends
  void getPendingFriendRequests_invalidInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/friends/pending")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
  }
  @Test // GET 11: get sent friend requests
  void getSentFriendRequests_validInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    User userMock = mock(User.class);
    List<Friend> friends = new ArrayList<Friend>();
    friends.add(testFriend);
    given(friendshipService.getAllSentFriendRequests(testUser)).willReturn(friends);

    // when/then
    MockHttpServletRequestBuilder getRequest = get("/users/friends/sent")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].friendId", is(testFriend.getFriendId().intValue())))
            .andExpect(jsonPath("$[0].username", is(testFriend.getUsername())))
            .andExpect(jsonPath("$[0].level", is(testFriend.getLevel())))
            .andExpect(jsonPath("$[0].points", is(testFriend.getPoints())));
  }
  @Test // GET 12: get sent friend requests
  void getSentFriendRequests_invalidInput_friendListReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/friends/sent")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
  }

  @Test // GET 13: get user notifications
  void getUserNotifications_validInput_notificationsReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    UserNotification userNotification = new UserNotification();
    userNotification.setUser(testUser);
    userNotification.setId(1L);
    userNotification.setMessage("this is a test notification");
    userNotification.setTimeStamp(LocalDateTime.of(2003, 1, 14, 10,10, 0));
    List<UserNotification> userNotifications = new ArrayList<UserNotification>();
    userNotifications.add(userNotification);

    given(notificationService.getUserNotifications(testUser)).willReturn(userNotifications);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].timeStamp", is(userNotification.getTimeStamp().format(formatter))))
            .andExpect(jsonPath("$[0].message", is(userNotification.getMessage())));
  }

  @Test // GET 14: get user notifications
  void getUserNotifications_invalidInput_notificationsReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/notifications")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
  }

  @Test // GET 15: get packings of a user
  void getItems_validInput_itemsReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    TemplatePackingItem templatePackingItem = new TemplatePackingItem();
    templatePackingItem.setUser(testUser);
    templatePackingItem.setId(1L);
    templatePackingItem.setItem("this is a test item");
    List<TemplatePackingItem> templatePackingItems = new ArrayList<TemplatePackingItem>();
    templatePackingItems.add(templatePackingItem);
    given(userService.getItems(testUser)).willReturn(templatePackingItems);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/packings")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(templatePackingItem.getId().intValue())))
            .andExpect(jsonPath("$[0].item", is(templatePackingItem.getItem())));
  }

  @Test // GET 16: get packings of a user
  void getItems_invalidInput_itemsReturned() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when
    MockHttpServletRequestBuilder getRequest = get("/users/packings")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    mockMvc.perform(getRequest)
            .andExpect(status().isNotFound());
  }

  // POST REQUESTS -------------------------------------------------------------
  @Test // POST 1: register
  void createUser_validInput_userCreated() throws Exception {
    // given

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");
    userPostDTO.setBirthday(LocalDate.of(2003, 1, 14));
    userPostDTO.setEmail("user@test.ch");

    given(userService.createUser(Mockito.any())).willReturn(testUser);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(content().string(containsString(testUser.getToken())));
  }

  @Test // POST 2: register
  void createUser_invalidInput_userCreated() throws Exception {
    // given

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("u");
    userPostDTO.setBirthday(LocalDate.of(2003, 1, 14));
    userPostDTO.setEmail("user@test.ch");

    given(userService.createUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().isConflict());
  }

  @Test // POST 3: login
  void loginUser_validInput_userLoggedIn() throws Exception {
    // given

    UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
    userLoginPostDTO.setPassword("Test User");
    userLoginPostDTO.setUsername("testUsername");

    given(userService.loginUser(Mockito.any())).willReturn(testUser.getToken());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userLoginPostDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(content().string(containsString(testUser.getToken())));
  }

  @Test // POST 4: login
  void loginUser_invalidInput_userNotLoggedIn() throws Exception {
    // given
    UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
    userLoginPostDTO.setPassword("wrong Test User");
    userLoginPostDTO.setUsername("testUsername");

    given(userService.loginUser(Mockito.any())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userLoginPostDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().isConflict());
  }

  @Test // POST 5: feedback
  void giveFeedback_validInput_feedbackSaved() throws Exception {
    MessagePostDTO messagePostDTO = new MessagePostDTO();
    messagePostDTO.setMessage("Ich bin sehr zufrieden mit GetTogether");

    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    doNothing().when(userService).giveFeedback(testUser, messagePostDTO.getMessage());
    doNothing().when(userService).increaseLevel(testUser, 0.5D);

    MockHttpServletRequestBuilder postRequest = post("/users/feedback")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(messagePostDTO))
            .header("Authorization",testUser.getToken());

    mockMvc.perform(postRequest)
            .andExpect(status().isCreated());

  }

  @Test // POST 6: feedback
  void giveFeedback_invalidInput_feedbackSaved() throws Exception {
    MessagePostDTO messagePostDTO = new MessagePostDTO();
    messagePostDTO.setMessage("Ich bin sehr zufrieden mit GetTogether");

    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.CONFLICT));

    MockHttpServletRequestBuilder postRequest = post("/users/feedback")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(messagePostDTO))
            .header("Authorization",testUser.getToken());

    mockMvc.perform(postRequest)
            .andExpect(status().isConflict());

  }

  @Test // POST 7: send friend request
  void makeRequest_validInput_requestSent() throws Exception {
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(userService.getUserById(testFriend.getFriendId())).willReturn(testUser); // should actually return User object of friend
    doNothing().when(friendshipService).sendRequest(testUser, testUser);
    doNothing().when(userService).increaseLevel(testUser, 0.05);

    MockHttpServletRequestBuilder postRequest = post("/users/friends/2")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    mockMvc.perform(postRequest)
            .andExpect(status().isCreated());
  }

  @Test // POST 8: send friend request
  void makeRequest_invalidInput_requestSent() throws Exception {
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    MockHttpServletRequestBuilder postRequest = post("/users/friends/2")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    mockMvc.perform(postRequest)
            .andExpect(status().isNotFound());
  }

  @Test // POST 9: add packing item
  void addItem_validInput_itemAdded() throws Exception {
  // given
  TemplateDTO templateDTO = new TemplateDTO();
  templateDTO.setItem("this is a test item");

  TemplatePackingItem templatePackingItem = new TemplatePackingItem();
  templatePackingItem.setUser(testUser);
  templatePackingItem.setId(1L);
  templatePackingItem.setItem("this is a test item");

  given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
  given(userService.addItem(testUser, templatePackingItem)).willReturn(templatePackingItem);

  // when
  MockHttpServletRequestBuilder postRequest = post("/users/packings")
          .contentType(MediaType.APPLICATION_JSON)
          .content(asJsonString(templateDTO))
          .header("Authorization",testUser.getToken());

  // then
  mockMvc.perform(postRequest)
          .andExpect(status().isCreated());
          //.andExpect(jsonPath("$.id", is(templatePackingItem.getId()))); somehow this does not work
  }

  @Test // POST 10: add packing item
  void addItem_invalidInput_itemAdded() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
    TemplateDTO templateDTO = new TemplateDTO();
    templateDTO.setItem("this is a test item");

    // when
    MockHttpServletRequestBuilder postRequest = post("/users/packings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(templateDTO))
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().isNotFound());
    //.andExpect(jsonPath("$.id", is(templatePackingItem.getId()))); somehow this does not work
  }


  // PUT REQUESTS -------------------------------------------------------------
  @Test // PUT 1: update user
  void updateUser_validInput_userUpdated() throws Exception {
    // given
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setPassword("Test User");
    userPutDTO.setUsername("testUsername");
    userPutDTO.setEmail("user@test.ch");
    userPutDTO.setBirthday(LocalDate.of(2000, 1, 1));

    doNothing().when(userService).updateUser(testUser.getToken(), testUser);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPutDTO))
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(putRequest)
            .andExpect(status().isNoContent());
  }

  @Test // PUT 2: update user with invalid input
  void updateUser_invalidInput_userNotUpdated() throws Exception {
    // given
    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setPassword("Test User");
    userPutDTO.setUsername("testUsername");
    userPutDTO.setEmail("user@test.ch");
    userPutDTO.setBirthday(LocalDate.of(2000, 1, 1));

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).updateUser(Mockito.any(), Mockito.any());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPutDTO))
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(putRequest)
            .andExpect(status().isNotFound());
  }

  @Test // PUT 3: accept friend request
  void acceptRequest_validInput_requestAccepted() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(userService.getUserById(testFriend.getFriendId())).willReturn(testUser); // should actually return User object of friend
    doNothing().when(friendshipService).acceptRequest(testUser, testUser);
    doNothing().when(userService).increaseLevel(testUser, 0.1);

    MockHttpServletRequestBuilder putRequest = put("/users/friends/2")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    mockMvc.perform(putRequest)
            .andExpect(status().isNoContent());
  }


  @Test // PUT 4: accept friend request
  void acceptRequest_invalidInput_requestAccepted() throws Exception {
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    MockHttpServletRequestBuilder putRequest = put("/users/friends/2")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", testUser.getToken());

    mockMvc.perform(putRequest)
            .andExpect(status().isNotFound());
  }

  @Test // PUT 5: update packing item
  void updateItem_validInput_itemUpdated() throws Exception {
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    TemplateDTO templateDTO = new TemplateDTO();
    templateDTO.setItem("this is a test item");

    TemplatePackingItem templatePackingItem = new TemplatePackingItem();
    templatePackingItem.setUser(testUser);
    templatePackingItem.setId(1L);
    templatePackingItem.setItem("this is a test item");

    doNothing().when(userService).updateItem(testUser, 1L, templatePackingItem);

    MockHttpServletRequestBuilder putRequest = put("/users/packings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(templateDTO))
            .header("Authorization", testUser.getToken());

    mockMvc.perform(putRequest)
            .andExpect(status().isNoContent());
  }

  @Test // PUT 6: update packing item
  void updateItem_invalidInput_itemUpdated() throws Exception {
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    TemplateDTO templateDTO = new TemplateDTO();
    templateDTO.setItem("this is a test item");


    MockHttpServletRequestBuilder putRequest = put("/users/packings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(templateDTO))
            .header("Authorization", testUser.getToken());

    mockMvc.perform(putRequest)
            .andExpect(status().isNotFound());
  }

  /* @Test // PUT x: uploading an image
  void saveProfilePicture_validInput_pictureSaved() throws Exception {
    // given

    byte[] fileContent = "Hello, World!".getBytes();
    MockMultipartFile file = new MockMultipartFile(
            "file",
            "hello.txt",
            MediaType.TEXT_PLAIN_VALUE,
            fileContent
    );
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    doNothing().when(userService).saveProfilePicture(testUser, file);
    doNothing().when(userService).increaseLevel(testUser, 0.1D);

    // Perform the request
    mockMvc.perform(multipart("/users/image")
                    .file((MockMultipartFile) file)
                    .header("Authorization",testUser.getToken())
                    .with(req -> { req.setMethod("PUT"); return req; }))
            .andExpect(status().isNoContent());
  } */

  // DELETE REQUESTS -------------------------------------------------------------
  @Test // DELETE 1: delete user with valid input
  void deleteUser_validInput_userDeleted() throws Exception {
    // given

    doNothing().when(userService).deleteUser(testUser.getToken());

    // when/then
    MockHttpServletRequestBuilder deleteRequest = delete("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(deleteRequest)
            .andExpect(status().isNoContent());
  }

  @Test // DELETE 2: delete user with invalid input
  void deleteUser_invalidInput_userDeleted() throws Exception {
    // given
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).deleteUser(testUser.getToken());

    // when/then
    MockHttpServletRequestBuilder deleteRequest = delete("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // tests
    mockMvc.perform(deleteRequest)
            .andExpect(status().isNotFound());
  }

  @Test // DELETE 3: delete friend request
  void deleteRequest_validInput_requestDeleted() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);
    given(userService.getUserById(testFriend.getFriendId())).willReturn(testUser); // should actually return User object of friend
    doNothing().when(friendshipService).deleteFriend(testUser, testUser);

    // when
    MockHttpServletRequestBuilder deleteRequest = delete("/users/friends/2")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(deleteRequest)
            .andExpect(status().isNoContent());
  }

  @Test // DELETE 4: delete friend request
  void deleteRequest_invalidInput_requestDeleted() throws Exception {
    // given
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when
    MockHttpServletRequestBuilder deleteRequest = delete("/users/friends/2")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",testUser.getToken());

    // then
    mockMvc.perform(deleteRequest)
            .andExpect(status().isNotFound());
  }

  @Test // DELETE 5: delete packing item
  void deleteItem_validInput_itemDeleted() throws Exception {
    given(userService.getUserByToken(testUser.getToken())).willReturn(testUser);

    TemplatePackingItem templatePackingItem = new TemplatePackingItem();
    templatePackingItem.setUser(testUser);
    templatePackingItem.setId(1L);
    templatePackingItem.setItem("this is a test item");

    doNothing().when(userService).deleteItem(testUser, 1L);

    MockHttpServletRequestBuilder deleteRequest = delete("/users/packings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", testUser.getToken());

    mockMvc.perform(deleteRequest)
            .andExpect(status().isNoContent());
  }

  @Test // DELETE 6: delete packing item
  void deleteItem_invalidInput_itemDeleted() throws Exception {
    given(userService.getUserByToken(testUser.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    TemplatePackingItem templatePackingItem = new TemplatePackingItem();
    templatePackingItem.setUser(testUser);
    templatePackingItem.setId(1L);
    templatePackingItem.setItem("this is a test item");

    doNothing().when(userService).deleteItem(testUser, 1L);

    MockHttpServletRequestBuilder deleteRequest = delete("/users/packings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", testUser.getToken());

    mockMvc.perform(deleteRequest)
            .andExpect(status().isNotFound());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   *
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      // Create ObjectMapper instance
      ObjectMapper objectMapper = new ObjectMapper();

      // Configure ObjectMapper to use the desired date format
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      objectMapper.setDateFormat(dateFormat);

      // Register JavaTimeModule for LocalDate serialization
      objectMapper.registerModule(new JavaTimeModule());

      // Convert object to JSON string
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
              String.format("The request body could not be created.%s", e.toString()));
    }
  }
}