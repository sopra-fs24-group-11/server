package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.FriendshipService;
import ch.uzh.ifi.hase.soprafs24.service.NotificationService;
import ch.uzh.ifi.hase.soprafs24.service.NullChecker;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;
  @MockBean
  private FriendshipService friendshipService;
  @MockBean
  private NotificationService notificationService;
  @MockBean
  private NullChecker nullChecker;

  // GET REQUESTS --------------------------------------------------------------
  @Test // GET 1: getting one user
  // given
  public void getUser_validInput_userReturned() throws Exception {
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setEmail("user@test.ch");
    user.setBirthday(LocalDate.of(2000, 1, 1));
    user.setToken("1d");
    user.setStatus(UserStatus.ONLINE);

    given(userService.getUserByToken(user.getToken())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",user.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(user.getId().intValue())))
            .andExpect(jsonPath("$.password", is(user.getPassword())))
            .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.email", is(user.getEmail())))
            .andExpect(jsonPath("$.birthday", is(user.getBirthday().toString())))
            .andExpect(jsonPath("$.status", is(user.getStatus().toString())));
  }

  @Test // GET 2: getting one user
  public void getUser_invalidInput_userNotReturned() throws Exception {
    // given
    User user = new User();
    user.setToken("1db");

    given(userService.getUserByToken(user.getToken())).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",user.getToken());

    // then
    mockMvc.perform(getRequest).andExpect(status().isNotFound());
  }

  @Test // GET 3: search with valid input
  public void searchUser_validInput_userListReturned() throws Exception {
    // given
    User user = new User();
    user.setToken("1db");

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
    user2.setUsername("testUsername");
    user2.setEmail("user@test.ch");
    user2.setBirthday(LocalDate.of(2000, 1, 1));
    user2.setToken("1d");
    user2.setLevel(1.0D);
    user2.setStatus(UserStatus.ONLINE);

    List<User> users = new ArrayList<User>();
    users.add(user1);
    users.add(user2);

    given(userService.getMatchingUsers(user.getToken(), "test")).willReturn(users);
    given(friendshipService.findFriendStatusSearch(Mockito.eq(user), Mockito.any())).willReturn(FriendshipStatusSearch.SENT);
    given(userService.getUserByToken(Mockito.any())).willReturn(user);


    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder getRequest = get("/users/search?name=test")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",user.getToken());

    mockMvc.perform(getRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id", is(user1.getId().intValue())))
            .andExpect(jsonPath("$[0].username", is(user1.getUsername())))
            .andExpect(jsonPath("$[0].level", is(user1.getLevel())))
            .andExpect(jsonPath("$[0].status", is("SENT")));
  }

  // POST REQUESTS -------------------------------------------------------------
  @Test // POST 1: register
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setEmail("user@test.ch");
    user.setBirthday(LocalDate.of(2000, 1, 1));
    user.setToken("1d");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");
    userPostDTO.setBirthday(LocalDate.of(2003, 1, 14));
    userPostDTO.setEmail("user@test.ch");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(content().string(containsString(user.getToken())));
  }

  @Test // POST 2: register
  public void createUser_invalidInput_userCreated() throws Exception {
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
  public void loginUser_validInput_userLoggedIn() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setEmail("user@test.ch");
    user.setBirthday(LocalDate.of(2000, 1, 1));
    user.setToken("1d");
    user.setStatus(UserStatus.ONLINE);

    UserLoginPostDTO userLoginPostDTO = new UserLoginPostDTO();
    userLoginPostDTO.setPassword("Test User");
    userLoginPostDTO.setUsername("testUsername");

    given(userService.loginUser(Mockito.any())).willReturn(user.getToken());

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userLoginPostDTO));

    // then
    mockMvc.perform(postRequest)
            .andExpect(status().isCreated())
            .andExpect(content().string(containsString(user.getToken())));
  }

  @Test // POST 4: login
  public void loginUser_invalidInput_userNotLoggedIn() throws Exception {
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

  // PUT REQUESTS -------------------------------------------------------------
  @Test // PUT 1: update user
  public void updateUser_validInput_userUpdated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setEmail("user@test.ch");
    user.setBirthday(LocalDate.of(2000, 1, 1));
    user.setToken("1d");
    user.setStatus(UserStatus.ONLINE);

    UserPutDTO userPutDTO = new UserPutDTO();
    userPutDTO.setPassword("Test User");
    userPutDTO.setUsername("testUsername");
    userPutDTO.setEmail("user@test.ch");
    userPutDTO.setBirthday(LocalDate.of(2000, 1, 1));

    doNothing().when(userService).updateUser(user.getToken(), user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder putRequest = put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(userPutDTO))
            .header("Authorization",user.getToken());

    // then
    mockMvc.perform(putRequest)
            .andExpect(status().isNoContent());
  }

  @Test // PUT 2: update user with invalid input
  public void updateUser_invalidInput_userNotUpdated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setEmail("user@test.ch");
    user.setBirthday(LocalDate.of(2000, 1, 1));
    user.setToken("1d");
    user.setStatus(UserStatus.ONLINE);

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
            .header("Authorization",user.getToken());

    // then
    mockMvc.perform(putRequest)
            .andExpect(status().isNotFound());
  }

  // DELETE REQUESTS -------------------------------------------------------------
  @Test // DELETE 1: delete user with valid input
  public void deleteUser_validInput_userDeleted() throws Exception {
    // given
    User user = new User();
    user.setToken("1d");

    doNothing().when(userService).deleteUser(user.getToken());

    // when/then
    MockHttpServletRequestBuilder deleteRequest = delete("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",user.getToken());

    // then
    mockMvc.perform(deleteRequest)
            .andExpect(status().isNoContent());
  }

  @Test // DELETE 2: delete user with invalid input
  public void deleteUser_invalidInput_userDeleted() throws Exception {
    // given
    User user = new User();
    user.setToken("1d");

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND)).when(userService).deleteUser(user.getToken());

    // when/then
    MockHttpServletRequestBuilder deleteRequest = delete("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization",user.getToken());

    // tests
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