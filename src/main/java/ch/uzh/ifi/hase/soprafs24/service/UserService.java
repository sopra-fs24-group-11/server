package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.entity.Image;
import ch.uzh.ifi.hase.soprafs24.repository.FeedbackRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.io.File;
import java.util.stream.Collectors;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final Random random = new Random();
  private final UserRepository userRepository;
  private final FeedbackRepository feedbackRepository;
  private final FriendshipService friendshipService;
  private final TripParticipantService tripParticipantService;
  private final NotificationService notificationService;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("feedbackRepository") FeedbackRepository feedbackRepository, FriendshipService friendshipService, TripParticipantService tripParticipantService, NotificationService notificationService) {
    this.userRepository = userRepository;
    this.feedbackRepository = feedbackRepository;
    this.friendshipService = friendshipService;
    this.tripParticipantService = tripParticipantService;
    this.notificationService = notificationService;
  }

  public User getUserByToken(String token) {
    User user = userRepository.findByToken(token);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    return user;
  }

  public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }


  public User createUser(User newUser) {
    checkIfUserNameExists(newUser);
    checkIfUserNameIsValid(newUser);

    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDate.now());
    newUser.setLevel(1.00);

    Image profileImage = new Image();
    profileImage.setProfilePicture(generateDefaultImage(newUser.getUsername()));
    newUser.setProfileImage(profileImage);

    newUser = userRepository.save(newUser);
    userRepository.flush();
    notificationService.createUserNotification(newUser, String.format("Welcome to Get-Together %s!", newUser.getUsername()));
    return newUser;
  }

  public String loginUser(User loginUser) {
    User existingUser = userRepository.findByUsername(loginUser.getUsername());
    if (existingUser == null || !Objects.equals(loginUser.getPassword(), existingUser.getPassword())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT,
              "The username and/or password provided are wrong. Please try again to log in!");
    }
    existingUser.setStatus(UserStatus.ONLINE);
    existingUser = userRepository.save(existingUser);
    userRepository.flush();
    return existingUser.getToken();
  }

  public void updateUser(String token, User user) {
    User existingUser = getUserByToken(token);
    checkIfUserNameIsValid(user);
    /*checkIfEmailIsValid(user);*/
    if (!Objects.equals(user.getUsername(), existingUser.getUsername())) {
      checkIfUserNameExists(user);
    }

    // Update the existing user with new information
    existingUser.setUsername(user.getUsername());
    existingUser.setBirthday(user.getBirthday());
    existingUser.setEmail(user.getEmail());
    existingUser.setPassword(user.getPassword());
    // Save the updated user
    existingUser = userRepository.save(existingUser);
    userRepository.flush();
    notificationService.createUserNotification(existingUser, "You updated your profile");
  }

  public void deleteUser(String token) {
    // user chose to delete their account -> delete everything with references to the user
    User user = getUserByToken(token);
    // To Do: Delete / revert all List Items!!!
    friendshipService.deleteAllForAUser(user);
    tripParticipantService.deleteAllForAUser(user);
    notificationService.deleteAllNotificationsForAUser(user);

    userRepository.deleteById(user.getId());
    userRepository.flush();
  }

  public void logoutUser(String token) {
    User user = getUserByToken(token);
    user.setStatus(UserStatus.OFFLINE);
    userRepository.save(user);
    userRepository.flush();
  }

  public List<User> getMatchingUsers(String token, String username) {
    User requester = getUserByToken(token);
    List<User> matchingUsers = userRepository.findAllByUsernameStartsWith(username);
    matchingUsers = matchingUsers.stream()
            .filter(user -> !Objects.equals(user.getId(), requester.getId()))
            .collect(Collectors.toList());
    return matchingUsers;
  }

  public void giveFeedback(User user, String message) {
    Feedback feedback = new Feedback();
    feedback.setUserId(user.getId());
    feedback.setMessage(message);
    feedbackRepository.save(feedback);
    feedbackRepository.flush();
    notificationService.createUserNotification(user, "Thank you for giving us feedback, we are happy to look at it!");
  }

  /**
   * This is a helper method that will check the uniqueness criteria of the
   * username and the name
   * defined in the User entity. The method will do nothing if the input is unique
   * and throw an error otherwise.
   *
   * @param userToBe
   * @throws org.springframework.web.server.ResponseStatusException
   * @see User
   */
  private void checkIfUserNameExists(User userToBe) {
    User userByUsername = userRepository.findByUsername(userToBe.getUsername());

    String baseErrorMessage = "The username provided is not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
    }
  }
  private void checkIfUserNameIsValid(User userToBe) {
    String un = userToBe.getUsername();
    String baseErrorMessage = "The username should have at least two characters, no spaces, and only contain letters, numbers or '-._'!";
    if (un.length()<2 || un.isBlank() || un.contains(" ") || !un.matches("^[a-zA-Z0-9\\-._]+$")) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
    }
  }


  public void increaseLevel(User user, double plus) {
    user.setLevel(user.getLevel()+plus);
    userRepository.save(user);
    userRepository.flush();
  }






  /**
   * Image Service
   */
  public void saveProfilePicture(User user, MultipartFile imageFile) throws IOException {
    byte[] imageData = imageFile.getBytes();
    Image profileImage = new Image();
    profileImage.setProfilePicture(imageData);
    user.setProfileImage(profileImage);
    userRepository.save(user);
    userRepository.flush();
  }

  public void deleteProfilePicture (User user){
    Image profileImage = user.getProfileImage();
    profileImage.setProfilePicture(generateDefaultImage(user.getUsername()));
    user.setProfileImage(profileImage);
    userRepository.save(user);
    userRepository.flush();
  }


  public byte[] getProfilePicture(User user) {
    Image profileImage = user.getProfileImage();
    /*viewImageInFolder(profileImage.getProfilePicture());*/
    return profileImage.getProfilePicture();
  }


  private byte[] generateDefaultImage(String userName) {
    int width = 1000;
    int height = 1000;
    String text = "--";
    if (userName.length() > 1) {
      text = userName.substring(0,2).toUpperCase();
    }

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = image.createGraphics();
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // random background colour
    Color bgColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    g2d.setColor(bgColor);
    g2d.fillRect(0, 0, width, height);

    Font font = new Font("Arial", Font.BOLD, 300);
    g2d.setFont(font);
    g2d.setColor(Color.WHITE);
    FontMetrics fm = g2d.getFontMetrics(); // Get font metrics for accurate text placement
    int textWidth = fm.stringWidth(text);
    int textHeight = fm.getHeight();
    int x = (width - textWidth) / 2; // Center the text horizontally
    int y = (height - textHeight) / 2 + fm.getAscent(); // Center the text vertically
    g2d.drawString(text, x, y);
    g2d.dispose();


    // Convert the BufferedImage to byte array
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(image, "jpg", baos);
      return baos.toByteArray();
    } catch (IOException e) {
      return null;
    }
  }

  private void viewImageInFolder(byte[] imageData) {
    String fileName = "default_image.jpg";
    try {
      File file = new File(fileName);
      ImageIO.write(ImageIO.read(new ByteArrayInputStream(imageData)), "jpg", file);
    } catch (IOException e) {
      return;
    }
  }

}
