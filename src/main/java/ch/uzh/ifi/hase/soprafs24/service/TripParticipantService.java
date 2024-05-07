package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.InvitationStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.TripParticipantRepository;
import ch.uzh.ifi.hase.soprafs24.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class TripParticipantService {
  private final TripParticipantRepository tripParticipantRepository;
  private final TripRepository tripRepository;
  private final ConnectionService connectionService;
  private final NotificationService notificationService;
  private final ListService listService;

  @Autowired
  public TripParticipantService(@Qualifier("tripParticipantRepository") TripParticipantRepository tripParticipantRepository, TripRepository tripRepository, ConnectionService connectionService, NotificationService notificationService, ListService listService) {
    this.tripParticipantRepository = tripParticipantRepository;
    this.tripRepository = tripRepository;
    this.connectionService = connectionService;
    this.notificationService = notificationService;
    this.listService = listService;
  }

  public List<TripParticipant> getTripParticipants(Trip trip) {
    return tripParticipantRepository.findAllByTrip(trip);
  }
  public List<TripParticipant> getTripParticipantsWhoHaveAccepted(Trip trip) {
    return tripParticipantRepository.findAllByTripAndStatus(trip, InvitationStatus.ACCEPTED);
  }

  public TripParticipant getTripParticipant(Trip trip, User user) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sie sind nicht Mitglied dieses Ausflugs.");
    }
    return participant;
  }

  public List<User> getTripUsers(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    List<User> users = new ArrayList<>();
    for (TripParticipant participant : participants) {
      users.add(participant.getUser());
    }
    return users;
  }

  public List<User> getTripUsersWhoHaveAccepted(Trip trip) {
    List<TripParticipant> participants = getTripParticipantsWhoHaveAccepted(trip);
    List<User> users = new ArrayList<>();
    for (TripParticipant participant : participants) {
      users.add(participant.getUser());
    }
    return users;
  }

  public List<User> getTripUsersWithoutAdmin(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    List<User> users = new ArrayList<>();
    for (TripParticipant participant : participants) {
      if (!Objects.equals(participant.getUser().getId(), trip.getAdministrator().getId())) {
        users.add(participant.getUser());
      }
    }
    return users;
  }


  public List<TripParticipant> getAllTripsOfAUser(User user) {
    return tripParticipantRepository.findAllByUser(user);
  }

  public void storeParticipants(Trip trip, User administrator, List<User> invited) {
    List<TripParticipant> newParticipants = new ArrayList<>();

    TripParticipant admin = new TripParticipant();
    admin.setUser(administrator);
    admin.setInvitator(administrator);
    admin.setTrip(trip);
    admin.setStatus(InvitationStatus.ACCEPTED);
    newParticipants.add(admin);
    notificationService.createUserNotification(administrator, String.format("Sie haben den Ausflug '%s' erstellt", trip.getTripName()));

    for(User user : invited) {
      TripParticipant participant = new TripParticipant();
      participant.setUser(user);
      participant.setInvitator(administrator);
      participant.setTrip(trip);
      newParticipants.add(participant);
      notificationService.createUserNotification(user, String.format("Sie wurden von %s zum Ausflug '%s' eingeladen", administrator.getUsername(), trip.getTripName()));
    }
    tripParticipantRepository.saveAll(newParticipants);
    tripParticipantRepository.flush();
  }

  public void storeParticipant(Trip trip, User administrator, User user) {
    TripParticipant newParticipant = new TripParticipant();

    newParticipant.setUser(user);
    newParticipant.setInvitator(administrator);
    newParticipant.setTrip(trip);

    tripParticipantRepository.save(newParticipant);
    tripParticipantRepository.flush();
    notificationService.createUserNotification(user, String.format("Sie wurden von %s zum Ausflug '%s' eingeladen", administrator.getUsername(), trip.getTripName()));
    notificationService.createTripNotification(trip, String.format("%s hat %s zum Ausflug hinzugefügt", administrator.getUsername(), user.getUsername()));
  }


  public void deleteAllForAUser(User user) {
    // this is for a user who deletes his account
    // TO DO: delete / revert list items
    List<TripParticipant> tripAdmins = tripParticipantRepository.findAllByUserAndTripAdministrator(user, user);
    if (!tripAdmins.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Sie können Ihren Account nicht löschen, solange Sie Administrator in mindestens einem Ausflug sind. Bitte löschen Sie diese Ausflüge. %d solche Ausflüge sind übrig.", tripAdmins.size()));
    }
    List<TripParticipant> tripParticipants = getAllTripsOfAUser(user);

    for (TripParticipant pa : tripParticipants) {
      connectionService.deleteConnection(pa);
      Trip trip = pa.getTrip();
      trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
      trip = tripRepository.save(trip);
      tripRepository.flush();
      notificationService.createTripNotification(trip, String.format("%s hat den Ausflug verlassen", user.getUsername()));
    }

    tripParticipantRepository.deleteAll(tripParticipants);
    tripParticipantRepository.flush();
  }

  public void isPartOfTripAndHasAccepted(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTripAndStatus(user, trip, InvitationStatus.ACCEPTED);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sie sind nicht Mitglied dieses Ausflugs. Fall Sie eingeladen wurden, nehmen Sie die Einladung im Dashboard an.");
    }
  }

  public List<TripParticipant> getTripHistory(User user) {
    return tripParticipantRepository.findAllByUserAndTripCompleted(user, true);
  }

  public List<TripParticipant> getFavoriteTrips(User user) {
    return tripParticipantRepository.findAllByUserAndFavouriteTrip(user, true);
  }

  public List<TripParticipant> getCurrentTrips(User user) {
    return tripParticipantRepository.findAllByUserAndTripCompletedAndStatus(user, false, InvitationStatus.ACCEPTED);
  }

  public List<TripParticipant> getUnansweredTrips(User user) {
    return tripParticipantRepository.findAllByUserAndTripCompletedAndStatus(user, false, InvitationStatus.PENDING);
  }

  public void markTripAsFavorite(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sie sind nicht Mitglied dieses Ausflugs.");
    }
    participant.setFavouriteTrip(!participant.isFavouriteTrip());
    tripParticipantRepository.save(participant);
    tripParticipantRepository.flush();
  }




  public void acceptInvitation(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sie wurden nicht zu diesem Ausflug eingeladen.");
    }
    participant.setStatus(InvitationStatus.ACCEPTED);
    tripParticipantRepository.save(participant);
    tripParticipantRepository.flush();
    notificationService.createTripNotification(trip, String.format("%s hat die Einladung angenommen.", user.getUsername()));
  }
  public void rejectInvitation(User user, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(user, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sie wurden nicht zu diesem Ausflug eingeladen.");
    }
    if (Objects.equals(trip.getAdministrator().getId(), user.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Als Administrator kann die Einladung nicht abgelehnt werden. Administratoren haben automatisch angenommen.");
    }
    if (participant.getStatus()==InvitationStatus.ACCEPTED) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Einladungen die bereits angenommen wurden können nicht abgelehnt werden - Verlassen Sie den Trip, falls Sie das wollen.");
    }

    // to do: revert / delete list items -> even if there shouldn't be any, it could happen (via postman) and a 500 error would be thrown
    connectionService.deleteConnection(participant);
    tripParticipantRepository.delete(participant);
    tripParticipantRepository.flush();

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    trip = tripRepository.save(trip);
    tripRepository.flush();

    notificationService.createTripNotification(trip, String.format("%s hat die Einladung abgelehnt", user.getUsername()));
  }

  public void leaveTrip(User leaver, Trip trip) {
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(leaver, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sie sind nicht Mitglied dieses Ausflugs.");
    }
    if (Objects.equals(trip.getAdministrator().getId(), leaver.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ernennen Sie einen neuen Admin, um den Ausflug zu verlassen..");
    }
    if (participant.getStatus()==InvitationStatus.PENDING) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Ausflüge, die noch nicht angenommen wurden, können nicht verlassen werden - Lehnen Sie die Einladung ab, falls Sie das wünschen.");
    }

    listService.deleteAllForAParticipant(participant);
    listService.revertAllForAParticipant(participant);
    connectionService.deleteConnection(participant);
    tripParticipantRepository.delete(participant);
    tripParticipantRepository.flush();

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    trip = tripRepository.save(trip);
    tripRepository.flush();

    notificationService.createTripNotification(trip, String.format("%s hat den Ausflug verlassen", leaver.getUsername()));
    notificationService.createUserNotification(leaver, String.format("Sie haben den Ausflug '%s' verlassen", trip.getTripName()));
  }

  public void removeMemberFromTrip(User userToBeRemoved, User requester, Trip trip) {
    if (!Objects.equals(trip.getAdministrator().getId(), requester.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Sie sind nicht Administator dieses Ausflugs.");
    }
    if (Objects.equals(userToBeRemoved.getId(), requester.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Sie können sich nicht selbst vom Ausflug entfernen.");
    }
    TripParticipant participant = tripParticipantRepository.findByUserAndTrip(userToBeRemoved, trip);
    if (participant == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s ist nicht Teil des Ausflugs und kann daher nicht entfernt werden.", userToBeRemoved.getUsername()));
    }

    listService.deleteAllForAParticipant(participant);
    listService.revertAllForAParticipant(participant);
    connectionService.deleteConnection(participant);
    tripParticipantRepository.delete(participant);
    tripParticipantRepository.flush();
    notificationService.createTripNotification(trip, String.format("%s hat %s vom Ausflug entfernt", requester.getUsername(), userToBeRemoved.getUsername()));
    notificationService.createUserNotification(userToBeRemoved, String.format("%s hat Sie vom Ausflug '%s' entfernt", requester.getUsername(), trip.getTripName()));

    trip.setNumberOfParticipants(trip.getNumberOfParticipants()-1);
    tripRepository.save(trip);
    tripRepository.flush();
  }



  public void deleteEverythingRelatedToATrip(Trip trip) {
    List<TripParticipant> participants = getTripParticipants(trip);
    for (TripParticipant pa : participants) {
      connectionService.deleteConnection(pa);
    }
    tripParticipantRepository.deleteAll(participants);
    tripParticipantRepository.flush();
  }

}
