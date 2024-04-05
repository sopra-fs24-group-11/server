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
  }

  public void createTripNotification(Trip trip, String message) {
    TripNotification note = reuseOldestTripNotificationIfTooMany(trip);
    note.setTrip(trip);
    note.setMessage(message);
    note.setTimeStamp(LocalDateTime.now());

    tripNotificationRepository.save(note);
    tripNotificationRepository.flush();
  }

  public void deleteAllForATrip(Trip trip) {
    tripNotificationRepository.deleteAllByTrip(trip);
    userNotificationRepository.flush();
  }

  public void deleteAllForAUser(User user) {
    userNotificationRepository.deleteAllByUser(user);
    userNotificationRepository.flush();
  }

  public UserNotification reuseOldestUserNotificationIfTooMany(User user) {
    List<UserNotification> notes = getUserNotifications(user);
    if (notes.size() > 24) {
      notes.sort(Comparator.comparing(UserNotification::getTimeStamp));
      return notes.get(0);
    } else {
      return new UserNotification();
    }
  }

  public TripNotification reuseOldestTripNotificationIfTooMany(Trip trip) {
    List<TripNotification> notes = getTripNotifications(trip);
    if (notes.size() > 24) {
      notes.sort(Comparator.comparing(TripNotification::getTimeStamp));
      return notes.get(0);
    } else {
      return new TripNotification();
    }
  }


}
