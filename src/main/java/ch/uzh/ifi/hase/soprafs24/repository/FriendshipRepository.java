package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("friendshipRepository")
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
  List<Friendship> findAllByFriend1(User user);
  List<Friendship> findAllByFriend2(User user);
  Friendship findByFriend1AndFriend2(User friend1, User friend2);
  List<Friendship> findAllByFriend1AndStatus(User user, FriendShipStatus status);
  List<Friendship> findAllByFriend2AndStatus(User user, FriendShipStatus status);
}
