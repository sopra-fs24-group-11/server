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
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Du kannst nicht mit dir selbst befreundet sein.");
    }
    if (friendshipRepository.findByFriend1AndFriend2(sender, receiver) != null || friendshipRepository.findByFriend1AndFriend2(receiver, sender) != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Freundschaft existiert bereits oder ist ausstehend.");
    }
    Friendship friendship = new Friendship();
    friendship.setFriend1(sender);
    friendship.setFriend2(receiver);
    friendship.setStatus(FriendShipStatus.PENDING);
    friendship.setPoints(0);
    friendshipRepository.save(friendship);
    friendshipRepository.flush();

    notificationService.createUserNotification(sender, String.format("Du hast eine Freundschaftsanfrage an %s gesendet", receiver.getUsername()));
    notificationService.createUserNotification(receiver, String.format("Du hast eine Freundschaftsanfrage von %s erhalten", sender.getUsername()));
  }

  public void acceptRequest(User acceptor, User requester) {
    Friendship friendship = friendshipRepository.findByFriend1AndFriend2(requester, acceptor);
    if (friendship == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Freundschaft existiert nicht.");
    }
    friendship.setStatus(FriendShipStatus.ACCEPTED);
    friendshipRepository.save(friendship);
    friendshipRepository.flush();

    notificationService.createUserNotification(acceptor, String.format("Du hast die Freundschaftsanfrage von %s angenommen", requester.getUsername()));
    notificationService.createUserNotification(requester, String.format("%s hat deine Freundschaftsanfrage angenommen", acceptor.getUsername()));
  }

  public void deleteFriend(User friend, User deleter) {
    Friendship friendship1 = friendshipRepository.findByFriend1AndFriend2(deleter, friend);
    Friendship friendship2 = friendshipRepository.findByFriend1AndFriend2(friend, deleter);
    if (friendship1 == null && friendship2 == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Freundschaft existiert nicht.");
    } else if (friendship1 != null && friendship2 != null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Es sollte keine symmetrische Freundschaft geben...");
    }
    if (friendship1 == null) {
      if (friendship2.getStatus()==FriendShipStatus.PENDING) {
        // receiver rejects request
        friendshipRepository.delete(friendship2);
        notificationService.createUserNotification(deleter, String.format("Du hast die Freundschaftsanfrage von %s abgelehnt", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s hat deine Freundschaftsanfrage abgelehnt", deleter.getUsername()));
      } else {
        // receiver deletes friendship
        friendshipRepository.delete(friendship2);
        notificationService.createUserNotification(deleter, String.format("Du hast die Freundschaft mit %s gelöscht", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s hat eure Freundschaft gelöscht", deleter.getUsername()));
      }
    } else {
      if (friendship1.getStatus()==FriendShipStatus.PENDING) {
        // requester withdraws request
        friendshipRepository.delete(friendship1);
        notificationService.createUserNotification(deleter, String.format("Du hast die Freundschaftsanfrage von %s zurückgezogen", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s hat deine Freundschaftsanfrage zurückgezogen", deleter.getUsername()));
      } else {
        // requester deletes friendship
        friendshipRepository.delete(friendship1);
        notificationService.createUserNotification(deleter, String.format("Du hast die Freundschaft mit %s gelöscht", friend.getUsername()));
        notificationService.createUserNotification(friend, String.format("%s hat eure Freundschaft gelöscht", deleter.getUsername()));
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
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Es sollte keine symmetrische Freundschaft geben...");
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

  public void increasePoints(List<User> users, double increase) {
    if (users.size()<2) {
      return;
    }
    for (User friend1 : users) {
      for (User friend2 : users) {
        Friendship friendship = friendshipRepository.findByFriend1AndFriend2(friend1, friend2);
        if (friendship != null) {
          friendship.setPoints(Math.max(Math.round((friendship.getPoints()+increase)*100)/100.0, 5.0));
          friendshipRepository.save(friendship);
          friendshipRepository.flush();
        }
      }
    }
  }
}
