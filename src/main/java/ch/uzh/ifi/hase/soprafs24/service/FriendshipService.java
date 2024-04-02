package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;
import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.FriendshipRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchingUserGetDTO;
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


@Service
@Transactional
public class FriendshipService {
  private final Logger log = LoggerFactory.getLogger(FriendshipService.class);

  private final FriendshipRepository friendshipRepository;

  @Autowired
  public FriendshipService(@Qualifier("friendshipRepository") FriendshipRepository friendshipRepository) {
    this.friendshipRepository = friendshipRepository;
  }

  public List<Friend> getAllReceivedFriendRequests(User user) {
    List<Friend> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend2(user);
    if (listOne != null) {
      for (Friendship friendship : listOne) {
        if (friendship.getStatus() == FriendShipStatus.PENDING) {
          User friend = friendship.getFriend2();
          Friend newFriend = new Friend();
          newFriend.setFriendId(friend.getId());
          newFriend.setLevel(friend.getLevel());
          newFriend.setUsername(friend.getUsername());
          newFriend.setPoints(friendship.getPoints());
          newFriend.setStatus(friendship.getStatus());
          result.add(newFriend);
        }
      }
    }
    return result;
  }

  public List<Friend> getAllSentFriendRequests(User user) {
    List<Friend> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend1(user);
    if (listOne != null) {
      for (Friendship friendship : listOne) {
        if (friendship.getStatus() == FriendShipStatus.PENDING) {
          User friend = friendship.getFriend2();
          Friend newFriend = new Friend();
          newFriend.setFriendId(friend.getId());
          newFriend.setLevel(friend.getLevel());
          newFriend.setUsername(friend.getUsername());
          newFriend.setPoints(friendship.getPoints());
          newFriend.setStatus(friendship.getStatus());
          result.add(newFriend);
        }
      }
    }
    return result;
  }

  public List<Friend> getAllPendingFriendRequests(User user) {
    List<Friend> result = new ArrayList<>();
    result.addAll(getAllSentFriendRequests(user));
    result.addAll(getAllReceivedFriendRequests(user));
    return result;
  }



  public List<Friend> getAllAcceptedFriends(User user) {
    List<Friend> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend1(user);
    List<Friendship> listTwo = friendshipRepository.findAllByFriend2(user);
    if (listOne == null && listTwo == null) {
      return new ArrayList<>(); // Return an empty list
    }
    if (listOne != null) {
      for (Friendship friendship : listOne) {
        if (friendship.getStatus() == FriendShipStatus.ACCEPTED) {
          User friend = friendship.getFriend2();
          Friend newFriend = new Friend();
          newFriend.setFriendId(friend.getId());
          newFriend.setLevel(friend.getLevel());
          newFriend.setUsername(friend.getUsername());
          newFriend.setPoints(friendship.getPoints());
          newFriend.setStatus(friendship.getStatus());
          result.add(newFriend);
        }
      }
    }
    if (listTwo != null) {
      for (Friendship friendship : listTwo) {
        if (friendship.getStatus() == FriendShipStatus.ACCEPTED) {
          User friend = friendship.getFriend1();
          Friend newFriend = new Friend();
          newFriend.setFriendId(friend.getId());
          newFriend.setLevel(friend.getLevel());
          newFriend.setUsername(friend.getUsername());
          newFriend.setPoints(friendship.getPoints());
          newFriend.setStatus(friendship.getStatus());
          result.add(newFriend);
        }
      }
    }
    return result;
  }

  public void sendRequest(User sender, User receiver) {
    if (sender == receiver) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Can't have a friendship with yourself");
    }
    if (friendshipRepository.findByFriend1AndFriend2(sender, receiver) != null || friendshipRepository.findByFriend1AndFriend2(receiver, sender) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Friendship already exists or is pending");
    }
    Friendship friendship = new Friendship();
    friendship.setFriend1(sender);
    friendship.setFriend2(receiver);
    friendship.setStatus(FriendShipStatus.PENDING);
    friendship.setPoints(0);
    friendshipRepository.save(friendship);
    friendshipRepository.flush();
    log.debug("Created Friendship Request: {}", friendship);
  }

  public void acceptRequest(User acceptor, User requester) {
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(requester, acceptor);
    if (friendship == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship does not exist");
    }
    friendship.setStatus(FriendShipStatus.ACCEPTED);
    friendship = friendshipRepository.save(friendship);
    friendshipRepository.flush();
    log.debug("Accepted Friendship: {}", friendship);
  }



  // Question: should we make three requests or only one?
  public void rejectRequest(User rejector, User requester) {
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(requester, rejector);
    if (friendship == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship does not exist");
    }
    friendshipRepository.deleteById(friendship.getId());
    log.debug("Deleted Friendship: {}", friendship);
  }
  public void withdrawRequest(User receiver, User requester) {
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(requester, receiver);
    if (friendship == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship does not exist");
    }
    friendshipRepository.deleteById(friendship.getId());
    log.debug("Deleted Friendship: {}", friendship);
  }

  public void deleteFriendship(User friend, User deleter) {
    Friendship friendship1 = friendshipRepository.findByFriend1AndFriend2(deleter, friend);
    Friendship friendship2 = friendshipRepository.findByFriend1AndFriend2(friend, deleter);
    if (friendship1 == null && friendship2 == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship does not exist");
    } else if (friendship1 == null) {
      friendshipRepository.deleteById(friendship2.getId());
      log.debug("Deleted Friendship: {}", friendship2);
    } else if (friendship2 == null) {
      friendshipRepository.deleteById(friendship1.getId());
      log.debug("Deleted Friendship: {}", friendship1);
    } else {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "There shouldn't be two symmetric friendships...");
    }


  }
  public void deleteFriend(User friend, User deleter) {
    Friendship friendship1 = friendshipRepository.findByFriend1AndFriend2(deleter, friend);
    Friendship friendship2 = friendshipRepository.findByFriend1AndFriend2(friend, deleter);
    if (friendship1 == null && friendship2 == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship does not exist");
    } else if (friendship1 != null && friendship2 != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "There shouldn't be two symmetric friendships...");
    }
    if (friendship1 == null) {
      if (friendship2.getStatus()==FriendShipStatus.PENDING) {
        // receiver rejects request
        friendshipRepository.deleteById(friendship2.getId());
        log.debug("Rejected Friendship: {}", friendship2);
      } else {
        // receiver deletes friendship
        friendshipRepository.deleteById(friendship2.getId());
        log.debug("Deleted Friendship: {}", friendship2);
      }
    } else if (friendship2 == null) {
      if (friendship1.getStatus()==FriendShipStatus.PENDING) {
        // requester withdraws request
        friendshipRepository.deleteById(friendship1.getId());
        log.debug("Rejected Friendship: {}", friendship1);
      } else {
        // requester deletes friendship
        friendshipRepository.deleteById(friendship1.getId());
        log.debug("Deleted Friendship: {}", friendship1);
      }
    }
  }

  public FriendshipStatusSearch findFriendStatusSearch (User searcher, User friend) {
    Friendship friendship1 = friendshipRepository.findByFriend1AndFriend2(searcher, friend);
    Friendship friendship2 = friendshipRepository.findByFriend1AndFriend2(friend, searcher);
    if (friendship1 == null && friendship2 == null) {
      return FriendshipStatusSearch.NOTHING;
    }
    else if (friendship1 != null && friendship2 != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "There shouldn't be two symmetric friendships...");
    }
    if (friendship1 == null) {
      if (friendship2.getStatus() == FriendShipStatus.PENDING) {
        // searcher has got pending request to answer
        return FriendshipStatusSearch.RECEIVED;
      }
      else {
        // completed friendship if searcher has initiated friendship
        return FriendshipStatusSearch.COMPLETED;
      }
    }
    else if (friendship2 == null) {
      if (friendship1.getStatus() == FriendShipStatus.PENDING) {
        // searcher has sent request and is waiting for response
        return FriendshipStatusSearch.SENT;
      }
      else {
        // completed friendship if searcher hasn't initiated friendship
        return FriendshipStatusSearch.COMPLETED;
      }
    }
    return FriendshipStatusSearch.NOTHING;
  }
}
