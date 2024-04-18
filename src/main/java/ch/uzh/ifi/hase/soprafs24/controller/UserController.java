package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.TemplatePackingItem;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserNotification;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.FriendshipService;
import ch.uzh.ifi.hase.soprafs24.service.NotificationService;
import ch.uzh.ifi.hase.soprafs24.service.NullChecker;
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
  private final NotificationService notificationService;

  UserController(UserService userService, FriendshipService friendshipService, NotificationService notificationService) {
    this.userService = userService;
    this.friendshipService = friendshipService;
    this.notificationService = notificationService;
  }

  @PostMapping("/users/register") // test exists
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public String createUser(@RequestBody UserPostDTO userPostDTO) {
    NullChecker.userPostDTOChecker(userPostDTO);
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User user = userService.createUser(userInput);
    return user.getToken();
  }

  @PostMapping("/users/login") // test exists
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public String loginUser(@RequestBody UserLoginPostDTO userLoginPostDTO) {
    NullChecker.userLoginPostDTOChecker(userLoginPostDTO);
    User userInput = DTOMapper.INSTANCE.convertUserLoginPostDTOtoEntity(userLoginPostDTO);
    return userService.loginUser(userInput);
  }
  @PutMapping("/users/logout") // test not really needed
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void logoutUser(@RequestHeader ("Authorization") String token) {
    userService.logoutUser(token);
  }

  @GetMapping("/users") // test exists
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO getUser(@RequestHeader ("Authorization") String token) {
    User user = userService.getUserByToken(token);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PutMapping("/users") // test exists
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@RequestHeader ("Authorization") String token, @RequestBody UserPutDTO userPutDTO) {
    NullChecker.userPutDTOChecker(userPutDTO);
    User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);
    userService.updateUser(token, user);
  }

  @DeleteMapping("/users") // test exists
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
      MatchingUserGetDTO searchedUser = DTOMapper.INSTANCE.convertEntityToMatchingUserGetDTO(user);
      searchedUser.setStatus(friendshipService.findFriendStatusSearch(userService.getUserByToken(token), user));
      userGetDTOs.add(searchedUser);

    }
    return userGetDTOs;
  }

  @PostMapping("/users/feedback")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void giveFeedback(@RequestHeader("Authorization") String token, @RequestBody MessagePostDTO messagePostDTO) {
    NullChecker.messagePostDTOChecker(messagePostDTO);
    User user = userService.getUserByToken(token);
    userService.giveFeedback(user, messagePostDTO.getMessage());
    userService.increaseLevel(user, 0.5);
  }


  // image
  @PutMapping("/users/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void saveProfilePicture(@RequestHeader("Authorization") String token, @RequestParam("image") MultipartFile imageFile) throws IOException {
    NullChecker.imageChecker(imageFile);
    User user = userService.getUserByToken(token);
    userService.saveProfilePicture(user, imageFile);
    userService.increaseLevel(user, 0.1);
  }
  @GetMapping("/users/image")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public byte[] getProfilePicture(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    return userService.getProfilePicture(user);
  }
  @DeleteMapping("/users/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteProfilePicture(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    userService.deleteProfilePicture(user);
  }


  // friends
  @GetMapping("/users/friends")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<FriendGetDTO> getAllAcceptedFriends(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<Friend> friends =  friendshipService.getAllAcceptedFriends(user);
    List<FriendGetDTO> friendGetDTOs = new ArrayList<>();

    for (Friend friend : friends) {
      FriendGetDTO tempDTO =  DTOMapper.INSTANCE.convertEntityToFriendGetDTO(friend);
      tempDTO.setStatus(userService.getUserById(friend.getFriendId()).getStatus());
      friendGetDTOs.add(tempDTO);
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
    userService.increaseLevel(sender, 0.05);
  }

  @PutMapping("/users/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void acceptRequest(@RequestHeader("Authorization") String token, @PathVariable Long friendId)  {
    User acceptor = userService.getUserByToken(token);
    User requester = userService.getUserById(friendId);
    friendshipService.acceptRequest(acceptor, requester);
    userService.increaseLevel(acceptor, 0.1);
  }
  @DeleteMapping("/users/friends/{friendId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteFriend(@RequestHeader("Authorization") String token, @PathVariable Long friendId) {
    User deleter = userService.getUserByToken(token);
    User friend = userService.getUserById(friendId);
    friendshipService.deleteFriend(friend, deleter);
  }

  @GetMapping("/users/notifications")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<NotificationGetDTO> getUserNotifications(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<UserNotification> notes = notificationService.getUserNotifications(user);

    List<NotificationGetDTO> notificationGetDTOs = new ArrayList<>();

    for (UserNotification note : notes) {
      notificationGetDTOs.add(DTOMapper.INSTANCE.convertUserNotificationToNotificationGetDTO(note));
    }
    return notificationGetDTOs;
  }



  @GetMapping("/users/packings")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<TemplateGetDTO> getItems(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<TemplatePackingItem> items = userService.getItems(user);
    List<TemplateGetDTO> templateGetDTOS = new ArrayList<>();

    for (TemplatePackingItem item : items) {
      templateGetDTOS.add(DTOMapper.INSTANCE.convertEntityToTemplateGetDTO(item));
    }
    return templateGetDTOS;
  }
  @PostMapping("/users/packings")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public TemplateGetDTO addItem(@RequestHeader("Authorization") String token, @RequestBody TemplateDTO templateDTO) {
    NullChecker.templateDTOChecker(templateDTO);
    User user = userService.getUserByToken(token);
    TemplatePackingItem item = DTOMapper.INSTANCE.convertTemplateDTOToEntity(templateDTO);
    return(DTOMapper.INSTANCE.convertEntityToTemplateGetDTO(userService.addItem(user, item)));
  }
  @PutMapping("/users/packings/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateItem(@RequestHeader("Authorization") String token, @PathVariable("itemId") Long itemId, @RequestBody TemplateDTO templateDTO) {
    NullChecker.templateDTOChecker(templateDTO);
    User user = userService.getUserByToken(token);
    TemplatePackingItem item = DTOMapper.INSTANCE.convertTemplateDTOToEntity(templateDTO);
    userService.updateItem(user, itemId, item);
  }
  @DeleteMapping("/users/packings/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteItem(@RequestHeader("Authorization") String token, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    userService.deleteItem(user, itemId);
  }
}
