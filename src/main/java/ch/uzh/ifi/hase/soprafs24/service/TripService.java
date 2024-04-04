package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Transactional
public class TripService {
  private final Logger log = LoggerFactory.getLogger(TripService.class);

  private final TripRepository tripRepository;
  private final UserService userService;
  private final TripParticipantService tripParticipantService;
  private final NotificationService notificationService;
  private final FriendshipService friendshipService;

  @Autowired
  public TripService(@Qualifier("tripRepository") TripRepository tripRepository, TripParticipantService tripParticipantService, UserService userService, NotificationService notificationService, FriendshipService friendshipService) {
    this.tripRepository = tripRepository;
    this.tripParticipantService = tripParticipantService;
    this.userService = userService;
    this.notificationService = notificationService;
    this.friendshipService = friendshipService;
  }

  public Trip getTripById(Long id) {
    return tripRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
  }

  public Long createTrip(Trip newTrip, User administrator, List<Long> userIds) {
    if (userIds.contains(administrator.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You invited yourself to the trip");
    }
    // remove duplicates
    List<User> invited = new ArrayList<>();
    Set<Long> set = new HashSet<>();
    for (Long id : userIds) {
      if (set.add(id)) {
        invited.add(userService.getUserById(id));
      }
    }
    // check if everyone invited is a friend
    List<User> friends = friendshipService.getAllAcceptedFriendsAsUsers(administrator);
    for (User invite : invited) {
      if (!friends.contains(invite)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "You can only invite friends to a trip.");
      }
    }
    newTrip.setAdministrator(administrator);
    int maximum = 10+(int)Math.floor(administrator.getLevel());
    if (maximum < invited.size() + 1) { // invited plus administrator
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Too many participants, size is limited to %d", maximum));
    }
    newTrip.setMaxParticipants(maximum);
    newTrip.setNumberOfParticipants(invited.size() + 1);


    tripRepository.save(newTrip);
    tripRepository.flush();
    // store every trip participant
    tripParticipantService.storeParticipants(newTrip, administrator, invited);
    notificationService.createTripNotification(newTrip, String.format("%s created the trip '%s'", administrator.getUsername(), newTrip.getTripName()));
    return newTrip.getId();
  }

  public void updateTrip(Long tripId, Trip updatedTrip, User administrator, List<Long> userIds) {
    if (!isAdmin(tripId, administrator)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not the admin of this trip");
    }
    if (userIds.contains(administrator.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You invited yourself to the trip");
    }
    List<User> invited = new ArrayList<>();
    Set<Long> set = new HashSet<>();
    for (Long id : userIds) {
      if (set.add(id)) {
        invited.add(userService.getUserById(id));
      }
    }

    Trip trip = getTripById(tripId);

    trip.setTripName(updatedTrip.getTripName());
    trip.setTripDescription(updatedTrip.getTripDescription());
    trip.setMeetUpTime(updatedTrip.getMeetUpTime());
    trip.setMeetUpPlace(updatedTrip.getMeetUpPlace());
    trip.setRating(updatedTrip.getRating());

    List<User> participants = tripParticipantService.getTripUsers(trip);
    invited.add(administrator);
    List<User> toAdd = new ArrayList<>();
    List<User> toDelete = new ArrayList<>();
    for (User user : participants) {
      if (!invited.contains(user)) {
        toDelete.add(user);
      }
    }

    for (User user : invited) {
      if (!participants.contains(user)) {
        toAdd.add(user);
      }
    }
    // check if everyone invited is a friend
    List<User> friends = friendshipService.getAllAcceptedFriendsAsUsers(administrator);
    for (User invite : toAdd) {
      if (!friends.contains(invite)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "You can only invite friends to a trip.");
      }
    }

    for (User user : toDelete) {
      tripParticipantService.removeMemberFromTrip(user, administrator, trip);
    }
    for (User user : toAdd) {
      tripParticipantService.storeParticipant(trip, administrator, user);
    }

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()+toAdd.size());
    trip = tripRepository.save(trip);
    tripRepository.flush();
    notificationService.createTripNotification(trip, String.format("%s updated the trip's details'", administrator.getUsername()));
    List<User> users = tripParticipantService.getTripUsers(trip);
    for (User user : users) {
      notificationService.createUserNotification(user, String.format("The trip %s has been updated! Go take a look!", trip.getTripName()));
    }
  }

  public boolean isAdmin(Long tripId, User requester) {
    Trip trip = getTripById(tripId);
    return Objects.equals(trip.getAdministrator().getId(), requester.getId());
  }

  public void newAdmin(Long tripId, User oldAdmin, User newAdmin) {
    if (!isAdmin(tripId, oldAdmin)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not the admin of this trip");
    }
    Trip trip = getTripById(tripId);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, newAdmin);
    if (participant.getStatus() == InvitationStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "New Admin you wanted to choose has not yet accepted the trip request");
    }
    trip.setAdministrator(newAdmin);
    trip = tripRepository.save(trip);
    tripRepository.flush();
    notificationService.createTripNotification(trip, String.format("%s announced %s as the new Administrator", oldAdmin.getUsername(), newAdmin.getUsername()));
  }

  public void deleteTrip(Long tripId, User requester) {
    if (!isAdmin(tripId, requester)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not the admin of this trip");
    }
    Trip trip = getTripById(tripId);

    List<User> users = tripParticipantService.getTripUsers(trip);
    for (User user : users) {
      notificationService.createUserNotification(user, String.format("The trip %s has been deleted", trip.getTripName()));
    }
    notificationService.deleteAllNotificationsForATrip(trip);
    tripParticipantService.deleteEverythingRelatedToATrip(trip);
    tripRepository.deleteById(tripId);
    tripRepository.flush();
  }



}
