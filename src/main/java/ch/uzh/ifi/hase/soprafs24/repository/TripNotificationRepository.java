package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.TripNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("tripNotificationRepository")
public interface TripNotificationRepository extends JpaRepository<TripNotification, Long> {

}
