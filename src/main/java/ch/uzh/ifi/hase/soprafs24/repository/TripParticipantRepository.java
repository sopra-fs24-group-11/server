package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("tripParticipantRepository")
public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
  List<TripParticipant> findAllByTrip(Trip trip);
  List<TripParticipant> findAllByUserAndFavouriteTrip(User user, boolean favouriteTrip);
  List<TripParticipant> findAllByUserAndTripCompletedAndStatus(User user, boolean completed, InvitationStatus status);
  List<TripParticipant> findAllByUserAndTripCompleted(User user, boolean completed);
  TripParticipant findByUserAndTrip(User user, Trip trip);
  List<TripParticipant> findAllByUser(User user);
}