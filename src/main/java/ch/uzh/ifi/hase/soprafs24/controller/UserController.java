package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.FriendshipService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {


  private final UserService userService;
  private final FriendshipService friendshipService;

  UserController(UserService userService, FriendshipService friendshipService) {
    this.userService = userService;
    this.friendshipService = friendshipService;
  }

  @PostMapping("/users/register")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public String createUser(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User user = userService.createUser(userInput);
    return user.getToken();
  }

  @PostMapping("/users/login")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public String loginUser(@RequestBody UserLoginPostDTO userLoginPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserLoginPostDTOtoEntity(userLoginPostDTO);
    return userService.loginUser(userInput);
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@RequestHeader ("Authorization") String token) {
    User user = userService.getUserByToken(token);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PutMapping("/users")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@RequestHeader ("Authorization") String token, @RequestBody UserPutDTO userPutDTO) {
    User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);
    userService.updateUser(token, user);
  }

  @DeleteMapping("/users")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteUser(@RequestHeader ("Authorization") String token) {
    userService.deleteUser(token);
  }


  @GetMapping("/users/search")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<MatchingUserGetDTO> getMatchingUsers(@RequestHeader("Authorization") String token, @RequestParam("name") String name) {
    List<User> users = userService.getMatchingUsers(token, name);
    List<MatchingUserGetDTO> userGetDTOs = new ArrayList<>();

    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToMatchingUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users/feedback")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void giveFeedback(@RequestHeader("Authorization") String token, @RequestBody MessageDTO messageDTO) {
    userService.giveFeedback(token, messageDTO.getMessage());
  }


  // image
  @PutMapping("/users/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void saveProfilePicture(@RequestHeader("Authorization") String token, @RequestParam("image") MultipartFile imageFile) throws IOException {
    // TO DO: Check for file size and type
    userService.saveProfilePicture(token, imageFile);
  }
  @GetMapping("/users/image")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public byte[] getProfilePicture(@RequestHeader("Authorization") String token) {
    return userService.getProfilePicture(token);
  }
  @DeleteMapping("/users/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteProfilePicture(@RequestHeader("Authorization") String token) {
    userService.deleteProfilePicture(token);
  }



  // friends
  // TO DO: notifications
  @GetMapping("/users/friends")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<FriendGetDTO> getAllAcceptedFriends(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<Friend> friends =  friendshipService.getAllAcceptedFriends(user);
    List<FriendGetDTO> friendGetDTOs = new ArrayList<>();

    for (Friend friend : friends) {
      friendGetDTOs.add(DTOMapper.INSTANCE.convertEntityToFriendGetDTO(friend));
    }
    return friendGetDTOs;
  }
  @GetMapping("/users/friends/requests")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<FriendGetDTO> getAllReceivedFriendRequests(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<Friend> friends = friendshipService.getAllReceivedFriendRequests(user);
    List<FriendGetDTO> friendGetDTOs = new ArrayList<>();

    for (Friend friend : friends) {
      friendGetDTOs.add(DTOMapper.INSTANCE.convertEntityToFriendGetDTO(friend));
    }
    return friendGetDTOs;
  }
  @GetMapping("/users/friends/pending")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<FriendGetDTO> getAllPendingFriendRequests(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<Friend> friends = friendshipService.getAllPendingFriendRequests(user);
    List<FriendGetDTO> friendGetDTOs = new ArrayList<>();

    for (Friend friend : friends) {
      friendGetDTOs.add(DTOMapper.INSTANCE.convertEntityToFriendGetDTO(friend));
    }
    return friendGetDTOs;
  }
  @GetMapping("/users/friends/sent")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<FriendGetDTO> getAllSentFriendRequests(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<Friend> friends = friendshipService.getAllSentFriendRequests(user);
    List<FriendGetDTO> friendGetDTOs = new ArrayList<>();

    for (Friend friend : friends) {
      friendGetDTOs.add(DTOMapper.INSTANCE.convertEntityToFriendGetDTO(friend));
    }
    return friendGetDTOs;
  }


  @PostMapping("/users/friends/{friendId}")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void makeRequest(@RequestHeader("Authorization") String token, @PathVariable Long friendId) {
    User sender = userService.getUserByToken(token);
    User receiver = userService.getUserById(friendId);
    friendshipService.sendRequest(sender, receiver);
  }
  @PutMapping("/users/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void acceptRequest(@RequestHeader("Authorization") String token, @PathVariable Long friendId)  {
    User acceptor = userService.getUserByToken(token);
    User requester = userService.getUserById(friendId);
    friendshipService.acceptRequest(acceptor, requester);
  }
  // like this?:
  @DeleteMapping("/users/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteFriend(@RequestHeader("Authorization") String token, @PathVariable Long friendId) {
    User deleter = userService.getUserByToken(token);
    User friend = userService.getUserById(friendId);
    friendshipService.deleteFriend(friend, deleter);
  }


  // or like this?:
  @DeleteMapping("/users/friends/{friendId}/reject")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void rejectRequest(@RequestHeader("Authorization") String token, @PathVariable Long friendId) {
    User rejector = userService.getUserByToken(token);
    User requester = userService.getUserById(friendId);
    friendshipService.rejectRequest(rejector, requester);
  }
  @DeleteMapping("/users/friends/{friendId}/withdraw")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void withdrawRequest(@RequestHeader("Authorization") String token, @PathVariable Long friendId) {
    User requester = userService.getUserByToken(token);
    User receiver = userService.getUserById(friendId);
    friendshipService.withdrawRequest(receiver, requester);
  }
  @DeleteMapping("/users/friends/{friendId}/delete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteFriendship(@RequestHeader("Authorization") String token, @PathVariable Long friendId) {
    User deleter = userService.getUserByToken(token);
    User friend = userService.getUserById(friendId);
    friendshipService.deleteFriendship(deleter, friend);
  }
}
