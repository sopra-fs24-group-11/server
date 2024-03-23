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

import java.util.*;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.awt.geom.Path2D;
import java.util.List;

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

    /*if (!Objects.equals(user.getToken(), token)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }*/

    byte[] imageData = imageFile.getBytes();
    System.out.println(Arrays.toString(imageData));
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

    /*if (!Objects.equals(user.getToken(), token)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }*/

    Image profileImage = user.getProfileImage();
    if (profileImage != null) {
      return profileImage.getProfilePicture();
    } else {
      // Return default image or throw an exception, depending on your requirements
      return generateDefaultImage();
    }
  }


  private byte[] generateDefaultImage() {
    int width = 100;
    int height = 100;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = image.createGraphics();

    // random background colour
    Random random = new Random();
    Color bgColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));

    g2d.setColor(bgColor);
    g2d.fillRect(0, 0, width, height);

    // Draw a yellow star in the middle of the image
    g2d.setColor(Color.YELLOW);

    int[] xPoints = {50, 55, 70, 57, 61, 50, 39, 43, 30, 45};
    int[] yPoints = {15, 35, 35, 45, 60, 50, 60, 45, 35, 35};

    Path2D star = new Path2D.Double();
    star.moveTo(xPoints[0], yPoints[0]);
    for (int i = 1; i < xPoints.length; i++) {
      star.lineTo(xPoints[i], yPoints[i]);
    }
    star.closePath();
    g2d.fill(star);
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


}
