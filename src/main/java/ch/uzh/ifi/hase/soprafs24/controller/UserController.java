package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserLoginDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

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

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }



  @PostMapping("/users/login")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserLoginDTO loginUser(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User createdUser = userService.loginUser(userInput);
    return DTOMapper.INSTANCE.convertEntityToUserLoginDTO(createdUser);
  }


  // image
  @PutMapping("/users/{userId}/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void saveProfilePicture(@PathVariable Long userId, @RequestHeader("token") String token, @RequestParam("image") MultipartFile imageFile) throws IOException {
    // TO DO: Check for file size and type
    userService.saveProfilePicture(userId, token, imageFile);
  }
  @GetMapping("/users/{userId}/image")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public byte[] getProfilePicture(@PathVariable Long userId, @RequestHeader("token") String token) {
    return userService.getProfilePicture(userId, token);
  }
  @DeleteMapping("/users/{userId}/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteProfilePicture(@PathVariable Long userId, @RequestHeader("token") String token) {
    userService.deleteProfilePicture(userId, token);
  }
}
