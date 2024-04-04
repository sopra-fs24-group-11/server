package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.TripParticipantRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TripParticipantService {

  private final Logger log = LoggerFactory.getLogger(TripParticipantService.class);

  private final TripParticipantRepository tripParticipantRepository;
  private final TripRepository tripRepository;
  private final ConnectionService connectionService;
  private final NotificationService notificationService;

  @Autowired
  public TripParticipantService(@Qualifier("tripParticipantRepository") TripParticipantRepository tripParticipantRepository, TripRepository tripRepository, ConnectionService connectionService, NotificationService notificationService) {
    this.tripParticipantRepository = tripParticipantRepository;
    this.tripRepository = tripRepository;
    this.connectionService = connectionService;
    this.notificationService = notificationService;
  }

  public List<TripParticipant> getTripParticipants(Trip trip) {
    return tripParticipantRepository.findAllByTrip(trip);
  }

  public TripParticipant getTripParticipant(Trip trip, User user) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not part of this trip");
    }
    return participant;
  }

  public List<User> getTripUsers(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    List<User> users = new ArrayList<>();
    for (TripParticipant participant : participants) {
      users.add(participant.getUser());
    }
    return users;
  }

  public List<User> getTripUsersWithoutAdmin(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    List<User> users = new ArrayList<>();
    for (TripParticipant participant : participants) {
      if (!Objects.equals(participant.getUser().getId(), trip.getAdministrator().getId())) {
        users.add(participant.getUser());
      }
    }
    return users;
  }


  public List<TripParticipant> getAllTripsOfAUser(User user) {
    return tripParticipantRepository.findAllByUser(user);
  }

  public void storeParticipants(Trip trip, User administrator, List<User> invited) {
    List<TripParticipant> newParticipants = new ArrayList<>();

    TripParticipant admin = new TripParticipant();
    admin.setUser(administrator);
    admin.setInvitator(administrator);
    admin.setTrip(trip);
    admin.setStatus(InvitationStatus.ACCEPTED);
    newParticipants.add(admin);
    notificationService.createUserNotification(administrator, String.format("You created the trip '%s'", trip.getTripName()));

    for(User user : invited) {
      TripParticipant participant = new TripParticipant();
      participant.setUser(user);
      participant.setInvitator(administrator);
      participant.setTrip(trip);
      newParticipants.add(participant);
      notificationService.createUserNotification(user, String.format("You were invited to the trip '%s' by %s", trip.getTripName(), administrator.getUsername()));
    }
    tripParticipantRepository.saveAll(newParticipants);
    tripParticipantRepository.flush();
  }

  public void storeParticipant(Trip trip, User administrator, User user) {
    TripParticipant newParticipant = new TripParticipant();

    newParticipant.setUser(user);
    newParticipant.setInvitator(administrator);
    newParticipant.setTrip(trip);

    tripParticipantRepository.save(newParticipant);
    tripParticipantRepository.flush();
    notificationService.createUserNotification(user, String.format("You were invited to the trip '%s' by %s", trip.getTripName(), administrator.getUsername()));
  }


  public void deleteAllForAUser(User user) {
    // this is for a user who deletes his account
    // TO DO: delete / revert list items
    List<TripParticipant> tripAdmins = tripParticipantRepository.findAllByUserAndTripAdministrator(user, user);
    if (!tripAdmins.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot delete your account if you are an admin");
    }
    List<TripParticipant> tripParticipants = getAllTripsOfAUser(user);

    for (TripParticipant pa : tripParticipants) {
      connectionService.deleteConnection(pa);
      Trip trip = pa.getTrip();
      trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
      trip = tripRepository.save(trip);
      tripRepository.flush();
      notificationService.createTripNotification(trip, String.format("%s left the trip", user.getUsername()));
    }

    tripParticipantRepository.deleteAll(tripParticipants);
    tripParticipantRepository.flush();
  }

  public void isPartOfTripAndHasAccepted(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTripAndStatus(user, trip, InvitationStatus.ACCEPTED);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
  }

  public List<Trip> getTripHistory(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompleted(user, true);
    List<Trip> trips = new ArrayList<>();
    for (TripParticipant participant : participants) {
      trips.add(participant.getTrip());
    }
    return trips;
  }

  public List<Trip> getFavoriteTrips(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndFavouriteTrip(user, true);
    List<Trip> trips = new ArrayList<>();
    for (TripParticipant participant : participants) {
      trips.add(participant.getTrip());
    }
    return trips;
  }

  public List<Trip> getCurrentTrips(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompletedAndStatus(user, false, InvitationStatus.ACCEPTED);
    List<Trip> trips = new ArrayList<>();
    for (TripParticipant participant : participants) {
      trips.add(participant.getTrip());
    }
    return trips;
  }

  public List<Trip> getUnansweredTrips(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompletedAndStatus(user, false, InvitationStatus.PENDING);
    List<Trip> trips = new ArrayList<>();
    for (TripParticipant participant : participants) {
      trips.add(participant.getTrip());
    }
    return trips;
  }

  public void markTripAsFavorite(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
    participant.setFavouriteTrip(!participant.isFavouriteTrip());
    tripParticipantRepository.save(participant);
    tripParticipantRepository.flush();
  }




  public void acceptInvitation(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
    participant.setStatus(InvitationStatus.ACCEPTED);
    tripParticipantRepository.save(participant);
    tripParticipantRepository.flush();
    notificationService.createTripNotification(trip, String.format("%s has accepted the trip invitation", user.getUsername()));
  }
  public void rejectInvitation(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
    if (Objects.equals(trip.getAdministrator().getId(), user.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin cannot reject an invitation, admin has automatically accepted");
    }
    if (participant.getStatus()==InvitationStatus.ACCEPTED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot reject an invitation that has already been accepted - You have to leave the trip instead");
    }

    // to do: revert / delete list items -> even if there shouldn't be any, it could happen (via postman) and a 500 error would be thrown
    connectionService.deleteConnection(participant);
    tripParticipantRepository.deleteById(participant.getId());
    tripParticipantRepository.flush();

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    trip = tripRepository.save(trip);
    tripRepository.flush();

    notificationService.createTripNotification(trip, String.format("%s has rejected the trip invitation", user.getUsername()));
  }

  public void leaveTrip(User leaver, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(leaver, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
    if (Objects.equals(trip.getAdministrator().getId(), leaver.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin cannot leave before announcing a new admin");
    }
    if (participant.getStatus()==InvitationStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot leave a trip that hasn't been accepted yet - You have to reject the invitation instead");
    }

    // to do: revert / delete list items
    connectionService.deleteConnection(participant);
    tripParticipantRepository.delete(participant);
    tripParticipantRepository.flush();

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    trip = tripRepository.save(trip);
    tripRepository.flush();

    notificationService.createTripNotification(trip, String.format("%s has left the trip", leaver.getUsername()));
  }

  public void removeMemberFromTrip(User userToBeRemoved, User requester, Trip trip) {
    if (!Objects.equals(trip.getAdministrator().getId(), requester.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not the admin of this trip");
    }
    if (Objects.equals(userToBeRemoved.getId(), requester.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You cannot remove yourself from the trip");
    }
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(userToBeRemoved, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User to be removed is not part of this trip");
    }
    // to do: revert / delete list items
    connectionService.deleteConnection(participant);
    tripParticipantRepository.delete(participant);
    tripParticipantRepository.flush();
    notificationService.createTripNotification(trip, String.format("%s removed %s from the trip", requester.getUsername(), userToBeRemoved.getUsername()));
    notificationService.createUserNotification(userToBeRemoved, String.format("%s removed you from the trip", requester.getUsername()));

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    tripRepository.save(trip);
    tripRepository.flush();
  }



  public void deleteEverythingRelatedToATrip(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    for (TripParticipant pa : participants) {
      connectionService.deleteConnection(pa);
    }
    tripParticipantRepository.deleteAll(participants);
    tripParticipantRepository.flush();
    // to do:  delete / revert each list item
  }

}
