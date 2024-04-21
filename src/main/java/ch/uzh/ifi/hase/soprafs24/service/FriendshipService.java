package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FriendShipStatus;
import ch.uzh.ifi.hase.soprafs24.constant.FriendshipStatusSearch;
import ch.uzh.ifi.hase.soprafs24.entity.Friend;
import ch.uzh.ifi.hase.soprafs24.entity.Friendship;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.FriendshipRepository;
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
  private final FriendshipRepository friendshipRepository;

  private final NotificationService notificationService;

  @Autowired
  public FriendshipService(@Qualifier("friendshipRepository") FriendshipRepository friendshipRepository, NotificationService notificationService) {
    this.friendshipRepository = friendshipRepository;
    this.notificationService = notificationService;
  }

  public void deleteAllForAUser(User user) {
    // this is for a user who chose to delete their account
    List<Friendship> friendships = getAllFriendshipsOfAUser(user);
    friendshipRepository.deleteAll(friendships);
    friendshipRepository.flush();
  }

  public List<Friend> getAllReceivedFriendRequests(User user) {
    List<Friend> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend2AndStatus(user, FriendShipStatus.PENDING);

    for (Friendship friendship : listOne) {
      User friend = friendship.getFriend1();
      Friend newFriend = new Friend();
      newFriend.setFriendId(friend.getId());
      newFriend.setLevel(friend.getLevel());
      newFriend.setUsername(friend.getUsername());
      newFriend.setPoints(friendship.getPoints());
      newFriend.setStatus(friendship.getStatus());
      result.add(newFriend);

    }

    return result;
  }

  public List<Friend> getAllSentFriendRequests(User user) {
    List<Friend> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend1AndStatus(user, FriendShipStatus.PENDING);

    for (Friendship friendship : listOne) {
      User friend = friendship.getFriend2();
      Friend newFriend = new Friend();
      newFriend.setFriendId(friend.getId());
      newFriend.setLevel(friend.getLevel());
      newFriend.setUsername(friend.getUsername());
      newFriend.setPoints(friendship.getPoints());
      newFriend.setStatus(friendship.getStatus());
      result.add(newFriend);
    }

    return result;
  }

  public List<Friend> getAllPendingFriendRequests(User user) {
    List<Friend> result = new ArrayList<>();
    result.addAll(getAllSentFriendRequests(user));
    result.addAll(getAllReceivedFriendRequests(user));
    return result;
  }

  public List<Friendship> getAllFriendshipsOfAUser(User user) {
    List<Friendship> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend1(user);
    List<Friendship> listTwo = friendshipRepository.findAllByFriend2(user);

    result.addAll(listOne);
    result.addAll(listTwo);

    return result;
  }

  public List<Friend> getAllAcceptedFriends(User user) {
    List<Friend> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend1AndStatus(user, FriendShipStatus.ACCEPTED);
    List<Friendship> listTwo = friendshipRepository.findAllByFriend2AndStatus(user, FriendShipStatus.ACCEPTED);

    for (Friendship friendship : listOne) {
      User friend = friendship.getFriend2();
      Friend newFriend = new Friend();
      newFriend.setFriendId(friend.getId());
      newFriend.setLevel(friend.getLevel());
      newFriend.setUsername(friend.getUsername());
      newFriend.setPoints(friendship.getPoints());
      newFriend.setStatus(friendship.getStatus());
      result.add(newFriend);

    }

    for (Friendship friendship : listTwo) {
      User friend = friendship.getFriend1();
      Friend newFriend = new Friend();
      newFriend.setFriendId(friend.getId());
      newFriend.setLevel(friend.getLevel());
      newFriend.setUsername(friend.getUsername());
      newFriend.setPoints(friendship.getPoints());
      newFriend.setStatus(friendship.getStatus());
      result.add(newFriend);

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

    notificationService.createUserNotification(sender, String.format("You sent a friend request to %s", receiver.getUsername()));
    notificationService.createUserNotification(receiver, String.format("You received a friend request from %s", sender.getUsername()));
  }

  public void acceptRequest(User acceptor, User requester) {
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(requester, acceptor);
    if (friendship == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Friendship does not exist");
    }
    friendship.setStatus(FriendShipStatus.ACCEPTED);
    friendshipRepository.save(friendship);
    friendshipRepository.flush();

    notificationService.createUserNotification(acceptor, String.format("You accepted the friend request from %s", requester.getUsername()));
    notificationService.createUserNotification(requester, String.format("%s accepted your friend request", acceptor.getUsername()));
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
        friendshipRepository.delete(friendship2);
        notificationService.createUserNotification(deleter, String.format("You rejected the friend request from %s", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s rejected your friend request", deleter.getUsername()));
      } else {
        // receiver deletes friendship
        friendshipRepository.delete(friendship2);
        notificationService.createUserNotification(deleter, String.format("You deleted your friendship with %s", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s deleted your friendship", deleter.getUsername()));
      }
    } else {
      if (friendship1.getStatus()==FriendShipStatus.PENDING) {
        // requester withdraws request
        friendshipRepository.delete(friendship1);
        notificationService.createUserNotification(deleter, String.format("You withdrew the friend request to %s", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s withdrew their friend request to you", deleter.getUsername()));
      } else {
        // requester deletes friendship
        friendshipRepository.delete(friendship1);
        notificationService.createUserNotification(deleter, String.format("You deleted your friendship with %s", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s deleted your friendship", deleter.getUsername()));
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
    else {
      if (friendship1.getStatus() == FriendShipStatus.PENDING) {
        // searcher has sent request and is waiting for response
        return FriendshipStatusSearch.SENT;
      }
      else {
        // completed friendship if searcher hasn't initiated friendship
        return FriendshipStatusSearch.COMPLETED;
      }
    }
  }

  public List<User> getAllAcceptedFriendsAsUsers(User user) {
    List<User> result = new ArrayList<>();
    List<Friendship> listOne = friendshipRepository.findAllByFriend1AndStatus(user, FriendShipStatus.ACCEPTED);
    List<Friendship> listTwo = friendshipRepository.findAllByFriend2AndStatus(user, FriendShipStatus.ACCEPTED);

    for (Friendship friendship : listOne) {
        result.add(friendship.getFriend2());
    }
    for (Friendship friendship : listTwo) {
        result.add(friendship.getFriend1());
    }
    return result;
  }

  public void increasePoints(List<User> users) {
    if (users.size()<2) {
      return;
    }
    for (User friend1 : users) {
      for (User friend2 : users) {
        Friendship friendship = friendshipRepository.findByFriend1AndFriend2(friend1, friend2);
        if (friendship != null) {
          friendship.setPoints(friendship.getPoints()+60);
          friendshipRepository.save(friendship);
          friendshipRepository.flush();
        }
      }
    }
  }
}
