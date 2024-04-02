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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
}
