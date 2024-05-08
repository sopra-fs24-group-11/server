package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.entity.Image;
import ch.uzh.ifi.hase.soprafs24.repository.FeedbackRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ImageRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TemplatePackingRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;
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
  private final Random random = new Random();
  private final UserRepository userRepository;
  private final ImageRepository imageRepository;
  private final FeedbackRepository feedbackRepository;
  private final FriendshipService friendshipService;
  private final TripParticipantService tripParticipantService;
  private final NotificationService notificationService;
  private final ListService listService;
  private final TemplatePackingRepository templatePackingRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository, @Qualifier("feedbackRepository") FeedbackRepository feedbackRepository, @Qualifier("imageRepository") ImageRepository imageRepository, FriendshipService friendshipService, TripParticipantService tripParticipantService, NotificationService notificationService, ListService listService, TemplatePackingRepository templatePackingRepository) {
    this.userRepository = userRepository;
    this.feedbackRepository = feedbackRepository;
    this.friendshipService = friendshipService;
    this.imageRepository = imageRepository;
    this.tripParticipantService = tripParticipantService;
    this.notificationService = notificationService;
    this.listService = listService;
    this.templatePackingRepository = templatePackingRepository;
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  public User getUserByToken(String token) {
    User user = userRepository.findByToken(token);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer nicht gefunden. Versuche dich auszuloggen und erneut einzuloggen.");
    }
    user.setLastOnline(LocalDateTime.now());
    user.setStatus(UserStatus.ONLINE);
    user = userRepository.save(user);
    userRepository.flush();
    return user;
  }

  public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Nutzer nicht gefunden."));
  }


  public User createUser(User newUser) {
    checkIfUserNameExists(newUser);
    checkIfUserNameIsValid(newUser);

    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDate.now());
    newUser.setLastOnline(LocalDateTime.now());
    newUser.setLevel(1.00);
    String encodedPassword = this.passwordEncoder.encode(newUser.getPassword());
    newUser.setPassword(encodedPassword);

    newUser = userRepository.save(newUser);
    userRepository.flush();
    notificationService.createUserNotification(newUser, String.format("Willkommen bei Get-Together %s!", newUser.getUsername()));

    Image profileImage = new Image();
    profileImage.setProfilePicture(generateDefaultImage(newUser.getUsername()));
    profileImage.setUserId(newUser.getId());
    imageRepository.save(profileImage);
    imageRepository.flush();

    return newUser;
  }

  public String loginUser(User loginUser) {
    User existingUser = userRepository.findByUsername(loginUser.getUsername());
    if (existingUser == null || !passwordEncoder.matches(loginUser.getPassword(), existingUser.getPassword())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzername oder Passwort sind falsch.");
    }
    existingUser.setStatus(UserStatus.ONLINE);
    existingUser = userRepository.save(existingUser);
    userRepository.flush();
    return existingUser.getToken();
  }

  public void updateUser(String token, User user) {
    User existingUser = getUserByToken(token);
    checkIfUserNameIsValid(user);
    if (!Objects.equals(user.getUsername(), existingUser.getUsername())) {
      checkIfUserNameExists(user);
    }

    // Update the existing user with new information
    existingUser.setUsername(user.getUsername());
    existingUser.setBirthday(user.getBirthday());
    existingUser.setEmail(user.getEmail());
    String encodedPassword = this.passwordEncoder.encode(user.getPassword());
    existingUser.setPassword(encodedPassword);
    // Save the updated user
    existingUser = userRepository.save(existingUser);
    userRepository.flush();
    notificationService.createUserNotification(existingUser, "Du hast dein Profil bearbeitet");
  }

  public void deleteUser(String token) {
    // user chose to delete their account -> delete everything with references to the user
    User user = getUserByToken(token);

    deleteAllItemsForAUser(user);
    listService.deleteAllForAUser(user.getId());
    listService.revertAllForAUser(user.getId());
    friendshipService.deleteAllForAUser(user);
    tripParticipantService.deleteAllForAUser(user);
    notificationService.deleteAllForAUser(user);

    imageRepository.deleteByUserId(user.getId());
    imageRepository.flush();
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
    notificationService.createUserNotification(user, "Danke für das Feedback! Gerne schauen wir es uns näher an!");
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

    String baseErrorMessage = "Benutzername bereits vergeben.";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
    }
  }
  private void checkIfUserNameIsValid(User userToBe) {
    String un = userToBe.getUsername();
    String baseErrorMessage = "Der Benutzername darf nur Buchstaben, Nummern und die Zeichen '-._' beinhalten!";
    if (un.length()<2 ||un.length()>30 || un.isBlank() || un.contains(" ") || !un.matches("^[a-zA-Z0-9\\-._]+$")) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, baseErrorMessage);
    }
  }


  public void increaseLevel(User user, double plus) {
    user.setLevel(user.getLevel()+plus);
    userRepository.save(user);
    userRepository.flush();
  }


  public List<TemplatePackingItem> getItems(User user) {
    return templatePackingRepository.findAllByUser(user);
  }
  public TemplatePackingItem getItem(Long itemId) {
    return templatePackingRepository.findById(itemId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Item nicht gefunden."));
  }
  public TemplatePackingItem addItem(User user, TemplatePackingItem item) {
    item.setUser(user);
    item = templatePackingRepository.save(item);
    templatePackingRepository.flush();
    return item;
  }
  public void deleteItem(User user, Long itemId) {
    checkIfItemExistsAndHasUser(user, itemId);
    templatePackingRepository.deleteById(itemId);
    templatePackingRepository.flush();
  }
  public void updateItem(User user, Long itemId, TemplatePackingItem updatedItem) {
    checkIfItemExistsAndHasUser(user, itemId);
    TemplatePackingItem item = getItem(itemId);
    item.setItem(updatedItem.getItem());
    templatePackingRepository.save(item);
    templatePackingRepository.flush();
  }
  public void deleteAllItemsForAUser(User user) {
    templatePackingRepository.deleteAllByUser(user);
    templatePackingRepository.flush();
  }
  public void checkIfItemExistsAndHasUser(User user, Long itemId) {
    TemplatePackingItem item = templatePackingRepository.findByIdAndUser(itemId, user);
    if (item == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item nicht gefunden.");
    }
  }


  /**
   * Image Service
   */
  public void saveProfilePicture(User user, MultipartFile imageFile) throws IOException {
    byte[] imageData = imageFile.getBytes();

    Image profileImage = imageRepository.findByUserId(user.getId());
    profileImage.setProfilePicture(imageData);
    imageRepository.save(profileImage);
    imageRepository.flush();
  }

  public void deleteProfilePicture (User user){
    Image profileImage = imageRepository.findByUserId(user.getId());
    profileImage.setProfilePicture(generateDefaultImage(user.getUsername()));
    imageRepository.save(profileImage);
    imageRepository.flush();
  }


  public byte[] getProfilePicture(User user) {
    Image profileImage = imageRepository.findByUserId(user.getId());
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
      return new byte[0];
    }
  }

  public List<Image> getImagesOfUsers(List<User> users) {
    List<Image> images = new ArrayList<>();
    for (User user: users) {
      images.add(imageRepository.findByUserId(user.getId()));
    }
    return images;
  }

  @Scheduled(fixedRate = 300000) // Check every 5 minutes
  public void markUsersAsOffline() {
    List<User> users = userRepository.findAllByStatusAndLastOnlineBefore(UserStatus.ONLINE, LocalDateTime.now().minusMinutes(5));
    for (User user : users) {
      user.setStatus(UserStatus.OFFLINE);
    }
  }
}
