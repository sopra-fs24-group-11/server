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

import java.util.List;
import java.util.Objects;

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

  public void createTrip(Trip newTrip, User administrator, List<User> invited, String meetUpPlace, String meetUpCode) {
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
  }

  public void updateTrip(Long tripId, User oldAdmin, Trip newTrip) {
    if (!isAdmin(tripId, oldAdmin)) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You are not the admin of this trip");
    }

    Trip trip = getTripById(tripId);
    if(newTrip.getAdministrator() != null) {
      trip.setAdministrator(newTrip.getAdministrator());
    }
    trip.setTripName(newTrip.getTripName());
    trip.setTripDescription(newTrip.getTripName());
    trip.setMeetUpTime(newTrip.getMeetUpTime());
    trip.setMeetUpPlace(newTrip.getMeetUpPlace());
    tripRepository.save(trip);
    tripRepository.flush();
    log.debug("Changed admin for Trip: {}", trip);
  }

  public boolean isAdmin(Long tripId, User requester) {
    Trip trip = getTripById(tripId);
    return Objects.equals(trip.getAdministrator(), requester);
  }

}
