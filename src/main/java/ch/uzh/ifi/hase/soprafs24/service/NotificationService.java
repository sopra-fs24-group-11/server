package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripNotification;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserNotification;
import ch.uzh.ifi.hase.soprafs24.repository.TripNotificationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserNotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class NotificationService {

  private final Logger log = LoggerFactory.getLogger(NotificationService.class);

  private final UserNotificationRepository userNotificationRepository;
  private final TripNotificationRepository tripNotificationRepository;

  public NotificationService (@Qualifier("userNotificationRepository") UserNotificationRepository userNotificationRepository, @Qualifier("tripNotificationRepository") TripNotificationRepository tripNotificationRepository) {
    this.userNotificationRepository = userNotificationRepository;
    this.tripNotificationRepository = tripNotificationRepository;
  }

  public List<UserNotification> getUserNotifications(User user) {
    return userNotificationRepository.findAllByUser(user);
  }

  public List<TripNotification> getTripNotifications(Trip trip) {
    return tripNotificationRepository.findAllByTrip(trip);
  }

  public void createUserNotification(User user, String message) {
    UserNotification note = reuseOldestUserNotificationIfTooMany(user);
    note.setUser(user);
    note.setMessage(message);
    note.setTimeStamp(LocalDateTime.now());

    userNotificationRepository.save(note);
    userNotificationRepository.flush();
    log.debug("Created User Notification for user: {}", user);
  }

  public void createTripNotification(Trip trip, String message) {
    TripNotification note = reuseOldestTripNotificationIfTooMany(trip);
    note.setTrip(trip);
    note.setMessage(message);
    note.setTimeStamp(LocalDateTime.now());

    tripNotificationRepository.save(note);
    tripNotificationRepository.flush();
    log.debug("Created Trip Notification for trip: {}", trip);
  }

  public void deleteAllNotificationsForATrip(Trip trip) {
    tripNotificationRepository.deleteAllByTrip(trip);
    userNotificationRepository.flush();
    log.debug("Deleted all TripNotifications for trip: {}", trip);
  }

  public void deleteAllNotificationsForAUser(User user) {
    userNotificationRepository.deleteAllByUser(user);
    userNotificationRepository.flush();
    log.debug("Deleted all UserNotifications for user: {}", user);
  }

  public UserNotification reuseOldestUserNotificationIfTooMany(User user) {
    List<UserNotification> notes = getUserNotifications(user);
    if (notes.size() > 10) {
      notes.sort(Comparator.comparing(UserNotification::getTimeStamp));
      log.debug("Reused oldest TripNotification for trip: {}", user);
      return notes.get(0);
    } else {
      return new UserNotification();
    }
  }

  public TripNotification reuseOldestTripNotificationIfTooMany(Trip trip) {
    List<TripNotification> notes = getTripNotifications(trip);
    if (notes.size() > 10) {
      notes.sort(Comparator.comparing(TripNotification::getTimeStamp));
      log.debug("Reused oldest TripNotification for trip: {}", trip);
      return notes.get(0);
    } else {
      return new TripNotification();
    }
  }


}
