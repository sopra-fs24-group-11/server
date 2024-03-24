package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.Image;
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
import java.util.*;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.awt.geom.Path2D;
import java.util.List;
import java.io.File;

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

  private final UserRepository userRepository;

  @Autowired
  public UserService(@Qualifier("userRepository") UserRepository userRepository) {
    this.userRepository = userRepository;
  }


  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.OFFLINE);
    checkIfUserNameExists(newUser);
    // saves the given entity but data is only persisted in the database once
    // flush() is called
    newUser = userRepository.save(newUser);
    userRepository.flush();

    log.debug("Created Information for User: {}", newUser);
    return newUser;
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

    String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
    if (userByUsername != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username", "is"));
    }
  }







  /**
   * Image Service
   */
  public void saveProfilePicture(Long userId, String token, MultipartFile imageFile) throws IOException {
    User user = userRepository.findById(userId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    // compare tokens
    byte[] imageData = imageFile.getBytes();
    Image profileImage = new Image();
    profileImage.setProfilePicture(imageData);
    user.setProfileImage(profileImage);
    userRepository.save(user);
    userRepository.flush();
    log.debug("Profile picture saved for User: {}", user);
  }
  public byte[] getProfilePicture(Long userId, String token) {
    User user = userRepository.findById(userId).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    // compare tokens
    /*Image profileImage = user.getProfileImage();
    return profileImage.getProfilePicture();*/
    byte[] imageData = generateDefaultImage("Masd");
    saveGeneratedImage(imageData, "default_image.jpg");
    return imageData;
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
    Random random = new Random();
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
      // Handle exception
      e.printStackTrace();
      return null;
    }
  }
  private void saveGeneratedImage(byte[] imageData, String fileName) {
    try {
      // Create a new file with the specified file name
      File file = new File(fileName);

      // Write the image data to the file
      ImageIO.write(ImageIO.read(new ByteArrayInputStream(imageData)), "jpg", file);

      System.out.println("Image saved as: " + file.getAbsolutePath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
