package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("tripNotificationRepository")
public interface TripNotificationRepository extends JpaRepository<TripNotification, Long> {
  List<TripNotification> findAllByTrip(Trip trip);
  void deleteAllByTrip(Trip trip);
}
