package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
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
  private final Logger log = LoggerFactory.getLogger(UserService.class);

  private final TripRepository tripRepository;

  // private final ConnectionService connectionService;

  private final TripParticipantService tripParticipantService;

  @Autowired
  public TripService(@Qualifier("tripRepository") TripRepository tripRepository, TripParticipantService tripParticipantService) {
    this.tripRepository = tripRepository;
    this.tripParticipantService = tripParticipantService;
  }

  public Trip getTripById(Long id) {
    return tripRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Trip not found"));
  }

  public Long createTrip(Trip newTrip, User administrator, List<User> invited, String meetUpPlace, String meetUpCode) {
    // Station station = connectionService.checkIfNameAndCodeAreCorrectAndTurnIntoStation(meetUpPlace, meetUpCode)
    newTrip.setAdministrator(administrator);
    int maximum = 10+(int)Math.floor(administrator.getLevel());
    if (maximum < invited.size() + 1) { // invited plus administrator
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Too many participants, size is limited to %d", maximum));
    }
    newTrip.setMaxParticipants(maximum);
    newTrip.setNumberOfParticipants(invited.size() + 1);

    // temporary here until connectionService works:
    Station station = new Station();
    station.setStationCode(meetUpCode);
    station.setStationName(meetUpPlace);


    newTrip.setMeetUpPlace(station);
    tripRepository.save(newTrip);
    tripRepository.flush();
    log.debug("Created Trip: {}", newTrip);
    // store every trip participant
    tripParticipantService.storeParticipants(newTrip, administrator, invited);
    return newTrip.getId();
  }

  public void updateTrip(Long tripId, Trip updatedTrip, User administrator, List<User> invited, String meetUpPlace, String meetUpCode) {
    if (meetUpCode == null || meetUpPlace == null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "MeetUpCode or MeetUpPlace are null");
    }
    if (!isAdmin(tripId, administrator)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not the admin of this trip");
    }

    Station station = new Station();
    station.setStationCode(meetUpCode);
    station.setStationName(meetUpPlace);

    Trip trip = getTripById(tripId);
    trip.setTripName(updatedTrip.getTripName());
    trip.setTripDescription(updatedTrip.getTripName());
    trip.setMeetUpTime(updatedTrip.getMeetUpTime());
    trip.setMeetUpPlace(station);

    List<User> participants = tripParticipantService.getTripParticipants(trip);
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

    for (User user : toDelete) {
      tripParticipantService.removeMemberFromTrip(user, administrator, trip);
    }
    for (User user : toAdd) {
      tripParticipantService.storeParticipant(trip, administrator, user);
    }

    tripRepository.save(trip);
    tripRepository.flush();
    log.debug("Updated Trip: {}", trip);
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
    trip.setAdministrator(newAdmin);
    tripRepository.save(trip);
    tripRepository.flush();
    log.debug("New administrator for trip: {}", trip);
  }

  public void deleteTrip(Long tripId, User requester) {
    if (!isAdmin(tripId, requester)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not the admin of this trip");
    }
    Trip trip = getTripById(tripId);

    /*tripParticipantService.deleteAllForATrip(trip);*/

    tripRepository.deleteById(tripId);
    tripRepository.flush();
    log.debug("Deleted Trip: {}", trip);
    // to do: delete all connections
    // make a loop and delete each one in tripparticipantservice and there delete / revert each list item
  }

}
