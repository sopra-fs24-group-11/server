package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.TripParticipantRepository;
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

  @Autowired
  public TripParticipantService(@Qualifier("tripParticipantRepository") TripParticipantRepository tripParticipantRepository) {
    this.tripParticipantRepository = tripParticipantRepository;
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

  public List<User> getTripParticipants(Trip trip) {
    List<TripParticipant> participants = tripParticipantRepository.findAllByTrip(trip);
    if (participants == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No participants found");
    }
    List<User> users = new ArrayList<>();
    for (TripParticipant participant : participants) {
      users.add(participant.getUser());
    }
    return users;
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
    List<TripParticipant> participants = tripParticipantRepository.findAllByUserAndTripCompleted(user, false);
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

    tripParticipantRepository.deleteById(participant.getId());
    tripParticipantRepository.flush();
    log.debug("Participant rejected trip invitation {}", participant);
    // to do: trip member count minus 1
  }

  public void leaveTrip(User leaver, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(leaver, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You are not a participant of this trip");
    }
    if (Objects.equals(trip.getAdministrator().getId(), leaver.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin cannot leave before announcing a new admin");
    }
    // to do: delete connections of participant and revert / delete list items
    tripParticipantRepository.deleteById(participant.getId());
    tripParticipantRepository.flush();
    log.debug("Participant rejected trip invitation {}", participant);
    // to do: trip member count minus 1
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
    // to do: trip member count minus 1
  }



}
