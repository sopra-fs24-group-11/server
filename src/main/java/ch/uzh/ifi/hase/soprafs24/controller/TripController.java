package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.ItemType;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TripController {

  private final TripService tripService;
  private final UserService userService;
  private final TripParticipantService tripParticipantService;
  private final ListService listService;
  private final ConnectionService connectionService;
  private final NotificationService notificationService;


  TripController(TripService tripService, UserService userService, TripParticipantService tripParticipantService, ListService listService, ConnectionService connectionService, NotificationService notificationService) {
    this.tripService = tripService;
    this.userService = userService;
    this.tripParticipantService = tripParticipantService;
    this.listService = listService;
    this.connectionService = connectionService;
    this.notificationService = notificationService;
  }

  @PostMapping("/trips/new")
  @ResponseStatus(HttpStatus.CREATED)
  public Long createTrip(@RequestHeader ("Authorization") String token, @RequestBody TripPostDTO tripPostDTO) {
    NullChecker.tripPostDTOChecker(tripPostDTO);
    List<Long> userIds = tripPostDTO.getParticipants();
    Trip tripInput = DTOMapper.INSTANCE.convertTripPostDTOtoEntity(tripPostDTO);
    User administrator = userService.getUserByToken(token);
    Long tripId = tripService.createTrip(tripInput, administrator, userIds);
    userService.increaseLevel(administrator, 0.2);
    return tripId;
  }
  @PutMapping("/trips/{tripId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTrip(@RequestHeader("Authorization") String token, @PathVariable Long tripId, @RequestBody TripPutDTO tripPutDTO) {
    NullChecker.tripPutDTOChecker(tripPutDTO);
    List<Long> userIds = tripPutDTO.getParticipants();
    Trip updatedTrip = DTOMapper.INSTANCE.convertTripPutDTOtoEntity(tripPutDTO);
    User administrator = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(administrator, trip);
    tripService.isOngoing(trip);
    tripService.updateTrip(trip, updatedTrip, administrator, userIds);
  }
  @GetMapping("/trips/{tripId}")
  @ResponseStatus(HttpStatus.OK)
  public BasicTripInfoGetDTO getTripInfo(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);
    BasicTripInfoGetDTO dto = DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(trip);
    dto.setFavourite(participant.isFavouriteTrip());
    return dto;
  }

  @GetMapping("/trips/{tripId}/participants")
  @ResponseStatus(HttpStatus.OK)
  public List<ParticipantGetDTO> getTripParticipants(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    Trip trip = tripService.getTripById(tripId);
    User requester = userService.getUserByToken(token);
    if (trip.getAdministrator()!=requester) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Du bist nicht Administrator dieser Reise.");
    }
    List<User> users = tripParticipantService.getTripUsersWithoutAdmin(trip);
    List<ParticipantGetDTO> participantGetDTOs = new ArrayList<>();

    for (User user : users) {
      participantGetDTOs.add(DTOMapper.INSTANCE.convertEntityToParticipantGetDTO(user));
    }
    return participantGetDTOs;
  }

  @GetMapping("/trips/history")
  @ResponseStatus(HttpStatus.OK)
  public List<BasicTripInfoGetDTO> getTripHistory(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<TripParticipant> participants = tripParticipantService.getTripHistory(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (TripParticipant participant : participants) {
      BasicTripInfoGetDTO dto = DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(participant.getTrip());
      dto.setFavourite(participant.isFavouriteTrip());
      basics.add(dto);
    }
    return basics;
  }
  @GetMapping("/trips/current")
  @ResponseStatus(HttpStatus.OK)
  public List<BasicTripInfoGetDTO> getCurrentTrips(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<TripParticipant> participants = tripParticipantService.getCurrentTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (TripParticipant participant : participants) {
      BasicTripInfoGetDTO dto = DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(participant.getTrip());
      dto.setFavourite(participant.isFavouriteTrip());
      basics.add(dto);
    }
    return basics;
  }
  @GetMapping("/trips/invitations")
  @ResponseStatus(HttpStatus.OK)
  public List<BasicTripInfoGetDTO> getUnansweredTrips(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<TripParticipant> participants = tripParticipantService.getUnansweredTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (TripParticipant participant : participants) {
      BasicTripInfoGetDTO dto = DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(participant.getTrip());
      dto.setFavourite(participant.isFavouriteTrip());
      basics.add(dto);
    }
    return basics;
  }
  @GetMapping("/trips/favorites")
  @ResponseStatus(HttpStatus.OK)
  public List<BasicTripInfoGetDTO> getFavoriteTrips(@RequestHeader("Authorization") String token) {
    User user = userService.getUserByToken(token);
    List<TripParticipant> participants = tripParticipantService.getFavoriteTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (TripParticipant participant : participants) {
      BasicTripInfoGetDTO dto = DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(participant.getTrip());
      dto.setFavourite(participant.isFavouriteTrip());
      basics.add(dto);
    }
    return basics;
  }

  @PutMapping("/trips/{tripId}/favorites")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markTripAsFavorite(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.markTripAsFavorite(user, trip);
  }

  @GetMapping("/trips/searchStation")
  @ResponseStatus(HttpStatus.OK)
  public List<Station> getStations (@RequestHeader("Authorization") String token, @RequestParam String start) {
    return ConnectionService.getLocationsName(start);
  }

  @GetMapping("/trips/{tripId}/startPoint")
  @ResponseStatus(HttpStatus.OK)
  public List<List<Connection>> getConnectionsByCode (@RequestHeader("Authorization") String token, @PathVariable Long tripId, @RequestParam("start") String start, @RequestParam("isLate") boolean isLate) {
    Trip trip = tripService.getTripById(tripId);
    String end = trip.getMeetUpPlace().getStationCode();
    String dateString = connectionService.getDateString(trip.getMeetUpTime());
    String timeString = connectionService.getTimeString(trip.getMeetUpTime());
    return ConnectionService.getConnectionsByCode(start, end, dateString, timeString, isLate);
  }

  @GetMapping("/trips/{tripId}/geoLocation")
  @ResponseStatus(HttpStatus.OK)
  public List<List<Connection>> getConnectionsByLocation (@RequestHeader("Authorization") String token, @PathVariable Long tripId, @RequestParam("x") String x, @RequestParam("y") String y, @RequestParam("isLate") boolean isLate) {
    Trip trip = tripService.getTripById(tripId);
    String end = trip.getMeetUpPlace().getStationCode();
    String start = ConnectionService.getLocationsCoord(x, y).getStationCode();
    String dateString = connectionService.getDateString(trip.getMeetUpTime());
    String timeString = connectionService.getTimeString(trip.getMeetUpTime());
    return ConnectionService.getConnectionsByCode(start, end, dateString, timeString, isLate);
  }

  @PutMapping("/trips/{tripId}/invitation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void acceptInvitation(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.acceptInvitation(user, trip);
    userService.increaseLevel(user, 0.1);
  }

  @DeleteMapping("/trips/{tripId}/invitation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void rejectInvitation(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.rejectInvitation(user, trip);
  }

  @GetMapping("/trips/{tripId}/admin")
  @ResponseStatus(HttpStatus.OK)
  public boolean isAdmin(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    return tripService.isAdmin(trip, user);
  }
  @PutMapping("/trips/{tripId}/admin/{adminId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void newAdmin(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("adminId") Long adminId) {
    User oldAdmin = userService.getUserByToken(token);
    User newAdmin = userService.getUserById(adminId);
    Trip trip = tripService.getTripById(tripId);
    tripService.newAdmin(trip, oldAdmin, newAdmin);
    userService.increaseLevel(newAdmin, 0.05);
  }
  @DeleteMapping("/trips/{tripId}/exit")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void leaveTrip(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.leaveTrip(user, trip);
  }

  @DeleteMapping("/trips/{tripId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTrip(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User requester = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.deleteTrip(trip, requester);
  }

  @GetMapping("/trips/{tripId}/pictures")
  @ResponseStatus(HttpStatus.OK)
  public List<MemberGetDTO> getMembersWithImages(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    List<User> users = tripParticipantService.getTripUsersWhoHaveAccepted(trip);
    List<Image> images = userService.getImagesOfUsers(users);
    List<MemberGetDTO> memberGetDTOs = new ArrayList<>();

    for (Image img: images) {
      memberGetDTOs.add(DTOMapper.INSTANCE.convertEntityToMemberGetDTO(img));
    }
    return memberGetDTOs;
  }



  @PostMapping("/trips/{tripId}/connection")
  @ResponseStatus(HttpStatus.CREATED)
  public void saveConnection(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody List<ConnectionDTO> connectionDTOS) {
    NullChecker.connectionDTOsChecker(connectionDTOS);
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    tripService.isOngoing(trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);

    List<ParticipantConnection> connections = new ArrayList<>();
    for (ConnectionDTO dto : connectionDTOS) {
      connections.add(DTOMapper.INSTANCE.convertConnectionDTOToEntity(dto));
    }
    connectionService.saveConnection(participant, connections);
    userService.increaseLevel(user, 0.2);
  }
  @PutMapping("/trips/{tripId}/connection")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateConnection(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody List<ConnectionDTO> connectionDTOS) {
    NullChecker.connectionDTOsChecker(connectionDTOS);
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    tripService.isOngoing(trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);

    List<ParticipantConnection> connections = new ArrayList<>();
    for (ConnectionDTO dto : connectionDTOS) {
      connections.add(DTOMapper.INSTANCE.convertConnectionDTOToEntity(dto));
    }
    connectionService.updateConnection(participant, connections);
  }

  @GetMapping("/trips/{tripId}/connection")
  @ResponseStatus(HttpStatus.OK)
  public List<ConnectionDTO> getConnection(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);
    List<ParticipantConnection> connections = connectionService.getConnection(participant);
    List<ConnectionDTO> dtos = new ArrayList<>();
    for (ParticipantConnection connection : connections) {
      dtos.add(DTOMapper.INSTANCE.convertEntityToConnectionDTO(connection));
    }
    return dtos;
  }
  @DeleteMapping("/trips/{tripId}/connection")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteConnection(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    tripService.isOngoing(trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);
    connectionService.deleteConnection(participant);
  }

  @GetMapping("/trips/{tripId}/connections")
  @ResponseStatus(HttpStatus.OK)
  public List<ConnectionAndUserDTO> getConnections(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    List<TripParticipant> participants = tripParticipantService.getTripParticipantsWhoHaveAccepted(trip);

    List<ConnectionAndUserDTO> dtos = new ArrayList<>();
    for (TripParticipant participant : participants) {
      ConnectionAndUserDTO dto = new ConnectionAndUserDTO();
      dto.setUsername(participant.getUser().getUsername());
      List<ParticipantConnection> connections = connectionService.getConnection(participant);
      List<ConnectionDTO> innerDTOs = new ArrayList<>();
      for (ParticipantConnection connection : connections) {
        innerDTOs.add(DTOMapper.INSTANCE.convertEntityToConnectionDTO(connection));
      }
      dto.setConnectionDTO(innerDTOs);
      dtos.add(dto);
    }
    return dtos;
  }

  @GetMapping("/trips/{tripId}/notifications")
  @ResponseStatus(HttpStatus.OK)
  public List<NotificationGetDTO> getTripNotifications(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    List<TripNotification> notes = notificationService.getTripNotifications(trip);

    List<NotificationGetDTO> notificationGetDTOs = new ArrayList<>();

    for (TripNotification note : notes) {
      notificationGetDTOs.add(DTOMapper.INSTANCE.convertEntityToNotificationGetDTO(note));
    }
    return notificationGetDTOs;
  }



  // Item can be updated / completed only by responsible person. If nobody is responsible, it can be updated but not completed.
  // Item can be deleted only by responsible person. If nobody is responsible, it can be deleted by everybody.
  @GetMapping("/trips/{tripId}/todos")
  @ResponseStatus(HttpStatus.OK)
  public List<ItemGetDTO> getTodos(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    TripParticipant tripParticipant = tripParticipantService.getTripParticipant(trip, user);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    List<Item> items = listService.getItems(trip, ItemType.TODO, tripParticipant);
    List<ItemGetDTO> itemGetDTOS = new ArrayList<>();
    for (Item item : items) {
      ItemGetDTO itemGetDTO = DTOMapper.INSTANCE.convertEntityToItemGetDTO(item);
      itemGetDTOS.add(itemGetDTO);
    }
    return itemGetDTOS;
  }

  @PutMapping("trips/{tripId}/todos/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTodos(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId, @RequestBody ItemPutDTO itemPutDTO) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.TODO);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasParticipantOrNone(itemId, selectedParticipant); // responsible person updates the item or item is open and anybody can adjust it
    Item updatedItem = DTOMapper.INSTANCE.convertItemPutDTOToEntity(itemPutDTO);
    listService.updateItem(itemId, updatedItem);
  }

  @PutMapping("trips/{tripId}/todos/{itemId}/responsible")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateResponsible(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);

    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.TODO);
    listService.checkIfItemIdHasNoParticipant(itemId); // item is free to choose
    // do not use checkIfItemHasParticipant, this function does not have the behaviour intended here
    listService.updateResponsible(itemId, selectedParticipant);
  }

  @DeleteMapping("trips/{tripId}/todos/{itemId}/responsible")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteResponsible(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);

    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.TODO);
    listService.checkIfItemIdHasParticipant(itemId, selectedParticipant);
    listService.deleteResponsible(itemId);
  }

  @PostMapping("trips/{tripId}/todos")
  @ResponseStatus(HttpStatus.CREATED)
  public ItemGetDTO createTodo(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody ItemPostDTO itemPostDTO) {
    NullChecker.itemPostDTOChecker(itemPostDTO);
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip,user);
    Item item = DTOMapper.INSTANCE.convertToDoPostDTOToEntity(itemPostDTO);
    item = listService.addItem(trip, item, ItemType.TODO, participant);
    return DTOMapper.INSTANCE.convertEntityToItemGetDTO(item);
  }

  @DeleteMapping("trips/{tripId}/todos/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTodo(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.TODO);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasParticipantOrNone(itemId, selectedParticipant); // responsible person deletes the item or item is free to delete
    listService.deleteItem(itemId);
  }

  @GetMapping("/trips/{tripId}/groupPackings")
  @ResponseStatus(HttpStatus.OK)
  public List<ItemGetDTO> getGroupPackings(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant tripParticipant = tripParticipantService.getTripParticipant(trip, user);
    List<Item> items = listService.getItems(trip, ItemType.GROUPPACKING, tripParticipant);
    List<ItemGetDTO> itemGetDTOS = new ArrayList<>();
    for (Item item : items) {
      ItemGetDTO itemGetDTO = DTOMapper.INSTANCE.convertEntityToItemGetDTO(item);
      itemGetDTOS.add(itemGetDTO);
    }
    return itemGetDTOS;
  }

  @PutMapping("trips/{tripId}/groupPackings/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateGroupPackings(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId, @RequestBody ItemPutDTO itemPutDTO) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.GROUPPACKING);
    Item updatedItem = DTOMapper.INSTANCE.convertItemPutDTOToEntity(itemPutDTO);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasParticipantOrNone(itemId, selectedParticipant); // responsible person updates the item or item is open and anybody can adjust it
    listService.updateItem(itemId, updatedItem);
  }

  @PutMapping("trips/{tripId}/groupPackings/{itemId}/responsible")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateGroupPackingsResponsible(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.GROUPPACKING);
    listService.checkIfItemIdHasNoParticipant(itemId); // item is free to choose
    // do not use checkIfItemHasParticipant, this function does not have the behaviour intended here
    listService.updateResponsible(itemId, selectedParticipant);
  }

  @DeleteMapping("trips/{tripId}/groupPackings/{itemId}/responsible")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGroupPackingsResponsible(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.GROUPPACKING);
    listService.checkIfItemIdHasParticipant(itemId, selectedParticipant);
    listService.deleteResponsible(itemId);
  }

  @PostMapping("trips/{tripId}/groupPackings")
  @ResponseStatus(HttpStatus.CREATED)
  public ItemGetDTO createGroupPacking(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody ItemPostDTO itemPostDTO) {
    NullChecker.itemPostDTOChecker(itemPostDTO);
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip,user);
    Item item = DTOMapper.INSTANCE.convertToDoPostDTOToEntity(itemPostDTO);
    item = listService.addItem(trip, item, ItemType.GROUPPACKING, participant);
    return DTOMapper.INSTANCE.convertEntityToItemGetDTO(item);
  }

  @DeleteMapping("trips/{tripId}/groupPackings/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteGroupPacking(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.GROUPPACKING);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasParticipantOrNone(itemId, selectedParticipant); // responsible person deletes the item or item is free to delete
    listService.deleteItem(itemId);
  }

  @GetMapping("/trips/{tripId}/individualPackings")
  @ResponseStatus(HttpStatus.OK)
  public List<ItemGetDTO> getIndividualPackings(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    List<Item> items = listService.getItems(trip, ItemType.INDIVIDUALPACKING, selectedParticipant);
    List<ItemGetDTO> itemGetDTOS = new ArrayList<>();
    for (Item item : items) {
      ItemGetDTO itemGetDTO = DTOMapper.INSTANCE.convertEntityToItemGetDTO(item);
      itemGetDTOS.add(itemGetDTO);
    }
    return itemGetDTOS;
  }

  @PutMapping("trips/{tripId}/individualPackings/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateIndividualPackings(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId, @RequestBody ItemPutDTO itemPutDTO) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.INDIVIDUALPACKING);
    listService.checkIfItemIdHasParticipant(itemId, selectedParticipant);
    Item updatedItem = DTOMapper.INSTANCE.convertItemPutDTOToEntity(itemPutDTO);
    listService.updateItem(itemId, updatedItem);
  }


  @PostMapping("trips/{tripId}/individualPackings")
  @ResponseStatus(HttpStatus.CREATED)
  public ItemGetDTO createIndividualPacking(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody ItemPostDTO itemPostDTO) {
    NullChecker.itemPostDTOChecker(itemPostDTO);
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    Item item = DTOMapper.INSTANCE.convertToDoPostDTOToEntity(itemPostDTO);
    item = listService.addItem(trip, item, ItemType.INDIVIDUALPACKING, selectedParticipant);
    return DTOMapper.INSTANCE.convertEntityToItemGetDTO(item);
  }

  @DeleteMapping("trips/{tripId}/individualPackings/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteIndividualPacking(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.checkIfItemIdHasTrip(itemId, trip);
    listService.checkIfItemIdHasType(itemId, ItemType.INDIVIDUALPACKING);
    listService.checkIfItemIdHasParticipant(itemId, selectedParticipant);
    listService.deleteItem(itemId);
  }

  @PostMapping("/trips/{tripId}/transfer/packings")
  @ResponseStatus(HttpStatus.CREATED)
  public void addItem(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripService.isOngoing(trip);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant selectedParticipant = tripParticipantService.getTripParticipant(trip,user);
    listService.transferList(trip, selectedParticipant, userService.getItems(user));
  }
}
