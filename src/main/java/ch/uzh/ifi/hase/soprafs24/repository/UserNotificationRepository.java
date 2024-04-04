package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("userNotificationRepository")
public interface UserNotificationRepository extends JpaRepository<UserNotification, Long>{
  List<UserNotification> findAllByUser(User user);
  void deleteAllByUser(User user);
}
