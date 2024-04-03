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

  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final TripParticipantRepository tripParticipantRepository;
  private final TripRepository tripRepository;


  @Autowired
  public TripParticipantService(@Qualifier("tripParticipantRepository") TripParticipantRepository tripParticipantRepository, TripRepository tripRepository) {
    this.tripParticipantRepository = tripParticipantRepository;
    this.tripRepository = tripRepository;
  }

  public List<TripParticipant> getTripParticipants(Trip trip) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByTrip(trip);
    if (participants == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No participants found");
    }
    return participants;
  }

  public void storeParticipants(Trip trip, User administrator, List<User> invited) {
    List<TripParticipant> newParticipants = new ArrayList<>();

    TripParticipant admin = new TripParticipant();
    admin.setUser(administrator);
    admin.setInvitator(administrator);
    admin.setTrip(trip);
    admin.setStatus(InvitationStatus.ACCEPTED);
    newParticipants.add(admin);

    for(User user : invited) {
      TripParticipant participant = new TripParticipant();
      participant.setUser(user);
      participant.setInvitator(administrator);
      participant.setTrip(trip);
      newParticipants.add(participant);
    }
    tripParticipantRepository.saveAll(newParticipants);
    tripParticipantRepository.flush();
    log.debug("Created Trip Participants: {}", newParticipants);
  }

  public void storeParticipant(Trip trip, User administrator, User user) {
    TripParticipant newParticipant = new TripParticipant();

    newParticipant.setUser(user);
    newParticipant.setInvitator(administrator);
    newParticipant.setTrip(trip);

    tripParticipantRepository.save(newParticipant);
    tripParticipantRepository.flush();
    log.debug("Created Trip Participant: {}", newParticipant);
  }

  public List<User> getTripUsers(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    List<User> users = new ArrayList<>();
    for (TripParticipant participant : participants) {
      users.add(participant.getUser());
    }
    return users;
  }


  public List<TripParticipant> getAllTripsOfAUser(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUser(user);
    return Objects.requireNonNullElseGet(participants, ArrayList::new);
  }

  public void deleteAllForAUser(User user) {
    // TO DO: delete all connections and list items
    List<TripParticipant> tripParticipants = getAllTripsOfAUser(user);
    tripParticipantRepository.deleteAll(tripParticipants);
    tripParticipantRepository.flush();
    log.debug("Deleted all friendships of user who chose to delete account");
  }

  public void isPartOfTripAndHasAccepted(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTripAndStatus(user, trip, InvitationStatus.ACCEPTED);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
  }

  public List<Trip> getTripHistory(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompleted(user, true);
    if (participants == null) {
      return new ArrayList<>(); // Return an empty list
    }
    List<Trip> trips = new ArrayList<>();
    for (TripParticipant participant : participants) {
      trips.add(participant.getTrip());
    }
    return trips;
  }

  public List<Trip> getFavoriteTrips(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndFavouriteTrip(user, true);
    if (participants == null) {
      return new ArrayList<>(); // Return an empty list
    }
    List<Trip> trips = new ArrayList<>();
    for (TripParticipant participant : participants) {
      trips.add(participant.getTrip());
    }
    return trips;
  }

  public List<Trip> getCurrentTrips(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompletedAndStatus(user, false, InvitationStatus.ACCEPTED);
    if (participants == null) {
      return new ArrayList<>(); // Return an empty list
    }
    List<Trip> trips = new ArrayList<>();
    for (TripParticipant participant : participants) {
      trips.add(participant.getTrip());
    }
    return trips;
  }

  public List<Trip> getUnansweredTrips(User user) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompletedAndStatus(user, false, InvitationStatus.PENDING);
    if (participants == null) {
      return new ArrayList<>(); // Return an empty list
    }
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
    log.debug("Marked trip as favourite for participant {}", participant);
  }




  public void acceptInvitation(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
    participant.setStatus(InvitationStatus.ACCEPTED);
    tripParticipantRepository.save(participant);
    tripParticipantRepository.flush();
    log.debug("Participant accepted trip invitation {}", participant);
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

    tripParticipantRepository.deleteById(participant.getId());
    tripParticipantRepository.flush();
    log.debug("Participant rejected trip invitation {}", participant);

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    tripRepository.save(trip);
    tripRepository.flush();
    log.debug("One member less in trip: {}", trip);
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

    // to do: delete connections of participant and revert / delete list items

    tripParticipantRepository.deleteById(participant.getId());
    tripParticipantRepository.flush();
    log.debug("Participant rejected trip invitation {}", participant);

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    tripRepository.save(trip);
    tripRepository.flush();
    log.debug("One member less in trip: {}", trip);
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
    tripParticipantRepository.deleteById(participant.getId());
    tripParticipantRepository.flush();
    log.debug("Admin removed participant from trip {}", participant);

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    tripRepository.save(trip);
    tripRepository.flush();
    log.debug("One member less in trip: {}", trip);
  }

  public TripParticipant getTripParticipant(Trip trip, User user) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User to be announced as new Admin is not part of this trip");
    }
    return participant;
  }

  public void deleteEverythingRelatedToATrip(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    tripParticipantRepository.deleteAll(participants);
    tripParticipantRepository.flush();
    log.debug("Deleted All Members of Trip: {}", trip);
    // to do: delete all connections
    // make a loop and delete each one in tripparticipantservice and there delete / revert each list item
  }

}
