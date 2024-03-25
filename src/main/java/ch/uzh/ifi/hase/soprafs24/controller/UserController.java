package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
  public UserGetDTO getUser(@RequestHeader ("token") String token) {
    User user = userService.getUserByToken(token);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
  }

  @PutMapping("/users")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@RequestHeader ("token") String token, @RequestBody UserPutDTO userPutDTO) {
    User user = DTOMapper.INSTANCE.convertUserPutDTOToEntity(userPutDTO);
    userService.updateUser(token, user);
  }

  @DeleteMapping("/users")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteUser(@RequestHeader ("token") String token) {
    userService.deleteUser(token);
  }


  // image
  @PutMapping("/users/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void saveProfilePicture(@RequestHeader("token") String token, @RequestParam("image") MultipartFile imageFile) throws IOException {
    // TO DO: Check for file size and type
    userService.saveProfilePicture(token, imageFile);
  }
  @GetMapping("/users/image")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public byte[] getProfilePicture(@RequestHeader("token") String token) {
    return userService.getProfilePicture(token);
  }
  @DeleteMapping("/users/image")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteProfilePicture(@RequestHeader("token") String token) {
    userService.deleteProfilePicture(token);
  }
}
