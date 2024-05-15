package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
  User findByToken(String name);
  User findByUsername(String username);
  List<User> findAllByUsernameStartsWithIgnoreCase(String username);
  List <User> findAllByStatusAndLastOnlineBefore(UserStatus userStatus,  LocalDateTime dateTime);
}
