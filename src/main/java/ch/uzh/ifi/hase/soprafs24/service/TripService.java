package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TripService {
  private final TripRepository tripRepository;
  private final UserService userService;
  private final TripParticipantService tripParticipantService;
  private final NotificationService notificationService;
  private final FriendshipService friendshipService;
  private final ListService listService;

  @Autowired
  public TripService(@Qualifier("tripRepository") TripRepository tripRepository, TripParticipantService tripParticipantService, UserService userService, NotificationService notificationService, FriendshipService friendshipService, ListService listService) {
    this.tripRepository = tripRepository;
    this.tripParticipantService = tripParticipantService;
    this.userService = userService;
    this.notificationService = notificationService;
    this.friendshipService = friendshipService;
    this.listService = listService;
  }

  public Trip getTripById(Long id) {
    return tripRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Die Reise konnte nicht gefunden werden."));
  }

  public Long createTrip(Trip newTrip, User administrator, List<Long> userIds) {
    if (userIds.contains(administrator.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Du hast sich selbst eingeladen.");
    }
    // remove duplicates
    List<User> invited = new ArrayList<>();
    Set<Long> set = new HashSet<>();
    for (Long id : userIds) {
      if (set.add(id)) {
        invited.add(userService.getUserById(id));
      }
    }
    // check trip size
    int maximum = 10+(int)Math.floor(administrator.getLevel());
    if (maximum < invited.size() + 1) { // invited plus administrator
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Zu viele Teilnehmer, die Maximalgrösse beträgt %d.", maximum));
    }
    // check if everyone invited is a friend
    List<User> friends = friendshipService.getAllAcceptedFriendsAsUsers(administrator);
    for (User invite : invited) {
      if (!friends.contains(invite)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Es können nur bestehende Freunde eingeladen werden. Versichere dich, Freundschaftsanfragen zuerst zu verschicken und anzunehmen.");
      }
    }
    newTrip.setAdministrator(administrator);
    newTrip.setMaxParticipants(maximum);
    newTrip.setNumberOfParticipants(invited.size() + 1);

    newTrip = tripRepository.save(newTrip);
    tripRepository.flush();
    // store every trip participant
    tripParticipantService.storeParticipants(newTrip, administrator, invited);
    notificationService.createTripNotification(newTrip, String.format("%s hat die Reise '%s' erstellt", administrator.getUsername(), newTrip.getTripName()));
    return newTrip.getId();
  }

  public void updateTrip(Trip trip, Trip updatedTrip, User administrator, List<Long> userIds) {
    if (!isAdmin(trip, administrator)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Nur Administratoren können die Details bearbeiten.");
    }
    if (userIds.contains(administrator.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Du hast dich selbst eingeladen.");
    }
    List<User> invited = new ArrayList<>();
    Set<Long> set = new HashSet<>();
    for (Long id : userIds) {
      if (set.add(id)) {
        invited.add(userService.getUserById(id));
      }
    }


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
    // check trip size
    if (trip.getMaxParticipants() < trip.getNumberOfParticipants()-toDelete.size()+toAdd.size()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Zu viele Teilnehmer, die Maximalgrösse beträgt %d.", trip.getMaxParticipants()));
    }
    // check if everyone invited is a friend
    List<User> friends = friendshipService.getAllAcceptedFriendsAsUsers(administrator);
    for (User invite : toAdd) {
      if (!friends.contains(invite)) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Es können nur bestehende Freunde eingeladen werden. Versichere dich, Freundschaftsanfragen zuerst zu verschicken und anzunehmen.");
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
    notificationService.createTripNotification(trip, String.format("%s hat die Details der Reise angepasst", administrator.getUsername()));
    List<User> users = tripParticipantService.getTripUsersWhoHaveAccepted(trip);
    for (User user : users) {
      notificationService.createUserNotification(user, String.format("Details des Auflugs '%s' wurde geändert", trip.getTripName()));
    }
  }

  public boolean isAdmin(Trip trip, User requester) {
    return Objects.equals(trip.getAdministrator().getId(), requester.getId());
  }

  public void newAdmin(Trip trip, User oldAdmin, User newAdmin) {
    if (!isAdmin(trip, oldAdmin)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Nur Administratoren können die Details bearbeiten.");
    }
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, newAdmin);
    if (participant.getStatus() == InvitationStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("%s hat die Einladung noch nicht angenommen.", participant.getUser().getUsername()));
    }
    trip.setAdministrator(newAdmin);
    trip = tripRepository.save(trip);
    tripRepository.flush();
    notificationService.createTripNotification(trip, String.format("%s hat %s zum Administrator ernannt.", oldAdmin.getUsername(), newAdmin.getUsername()));
  }

  public void deleteTrip(Trip trip, User requester) {
    if (!isAdmin(trip, requester)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Nur Administratoren können die Details bearbeiten.");
    }

    List<User> users = tripParticipantService.getTripUsers(trip);
    for (User user : users) {
      notificationService.createUserNotification(user, String.format("Die Reise '%s' wurde gelöscht.", trip.getTripName()));
    }
    listService.deleteAllForATrip(trip);
    notificationService.deleteAllForATrip(trip);
    tripParticipantService.deleteEverythingRelatedToATrip(trip);
    tripRepository.delete(trip);
    tripRepository.flush();
  }

  public void isOngoing(Trip trip) {
    if (trip.isCompleted()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Die Reise ist beendet, Du kannst nichts mehr ändern! Die Reise löschen oder verlassen ist noch möglich.");
    }
  }

  @Scheduled(fixedRate = 15000) // Check every 15 seconds
  public void markTripsAsCompleted() {
    List<Trip> ongoingTrips = tripRepository.findAllByCompletedFalseAndMeetUpTimeBefore(LocalDateTime.now());
    for (Trip trip : ongoingTrips) {
      trip.setCompleted(true);
      tripRepository.save(trip);
      notificationService.createTripNotification(trip, "Die Reise ist beendet! Hurraa!");
      List<User> users = tripParticipantService.getTripUsersWhoHaveAccepted(trip);
      friendshipService.increasePoints(users, (double)trip.getNumberOfParticipants()/30);
      for (User u : users) {
        notificationService.createUserNotification(u, String.format("Die Reise '%s' ist beendet.", trip.getTripName()));
        userService.increaseLevel(u, (double)trip.getNumberOfParticipants()/10);
      }
    }
    tripRepository.flush();
  }

}
