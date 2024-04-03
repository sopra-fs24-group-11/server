package ch.uzh.ifi.hase.soprafs24.controller;

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



  TripController(TripService tripService, UserService userService, TripParticipantService tripParticipantService, ListService listService, ConnectionService connectionService) {
    this.tripService = tripService;
    this.userService = userService;
    this.tripParticipantService = tripParticipantService;
    this.listService = listService;
    this.connectionService = connectionService;
  }

  @PostMapping("/trips/new")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public Long createTrip(@RequestHeader ("Authorization") String token, @RequestBody TripPostDTO tripPostDTO) {
    NullChecker.tripPostDTOChecker(tripPostDTO);
    List<Long> userIds = tripPostDTO.getParticipants();
    Trip tripInput = DTOMapper.INSTANCE.convertTripPostDTOtoEntity(tripPostDTO);
    User administrator = userService.getUserByToken(token);
    return tripService.createTrip(tripInput, administrator, userIds);
  }
  @PutMapping("/trips/{tripId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateTrip(@RequestHeader("Authorization") String token, @PathVariable Long tripId, @RequestBody TripPutDTO tripPutDTO) {
    NullChecker.tripPutDTOChecker(tripPutDTO);
    List<Long> userIds = tripPutDTO.getParticipants();
    Trip updatedTrip = DTOMapper.INSTANCE.convertTripPutDTOtoEntity(tripPutDTO);
    User administrator = userService.getUserByToken(token);
    tripService.updateTrip(tripId, updatedTrip, administrator, userIds);
  }
  @GetMapping("/trips/{tripId}")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public BasicTripInfoGetDTO getTripInfo(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    return (DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(trip));

  }

  @GetMapping("/trips/{tripId}/participants")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<ParticipantGetDTO> getTripParticipants(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    Trip trip = tripService.getTripById(tripId);
    List<User> users = tripParticipantService.getTripUsers(trip);
    List<ParticipantGetDTO> participantGetDTOs = new ArrayList<>();
    if (!users.contains(userService.getUserByToken(token))) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not part of this trip and you can't see its participants");
    }
    for (User user : users) {
      participantGetDTOs.add(DTOMapper.INSTANCE.convertEntityToParticipantGetDTO(user));
    }
    return participantGetDTOs;
  }

  @GetMapping("/trips/history")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<BasicTripInfoGetDTO> getTripHistory(@RequestHeader("Authorization") String token) {
    // get all trips of a user no matter the status
    User user = userService.getUserByToken(token);
    List<Trip> trips = tripParticipantService.getTripHistory(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (Trip trip : trips) {
      basics.add(DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(trip));
    }
    return basics;
  }
  @GetMapping("/trips/current")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<BasicTripInfoGetDTO> getCurrentTrips(@RequestHeader("Authorization") String token) {
    // get all ongoing trips of a user
    User user = userService.getUserByToken(token);
    List<Trip> trips = tripParticipantService.getCurrentTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (Trip trip : trips) {
      basics.add(DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(trip));
    }
    return basics;
  }
  @GetMapping("/trips/invitations")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<BasicTripInfoGetDTO> getUnansweredTrips(@RequestHeader("Authorization") String token) {
    // get all unanswered trips of a user
    User user = userService.getUserByToken(token);
    List<Trip> trips = tripParticipantService.getUnansweredTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (Trip trip : trips) {
      basics.add(DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(trip));
    }
    return basics;
  }
  @GetMapping("/trips/favorites")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<BasicTripInfoGetDTO> getFavoriteTrips(@RequestHeader("Authorization") String token) {
    // get all favourite trips of a user
    User user = userService.getUserByToken(token);
    List<Trip> trips = tripParticipantService.getFavoriteTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (Trip trip : trips) {
      basics.add(DTOMapper.INSTANCE.convertEntityToBasicTripInfoGetDTO(trip));
    }
    return basics;
  }

  @PutMapping("/trips/{tripId}/favorites")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void markTripAsFavorite(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.markTripAsFavorite(user, trip);
  }

  @GetMapping("/trips/searchStation")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<Station> getStations (@RequestHeader("Authorization") String token, @RequestParam String start) {
    return ConnectionService.getLocationsName(start);
  }

  @GetMapping("/trips/{tripId}/startPoint")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<List<Connection>> getConnectionsByCode (@RequestHeader("Authorization") String token, @PathVariable Long tripId, @RequestParam String start) {
    Trip trip = tripService.getTripById(tripId);
    String end = trip.getMeetUpPlace().getStationCode();
    return ConnectionService.getConnectionsByCode(start, end);
  }

  @GetMapping("/trips/{tripId}/geoLocation")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<List<Connection>> getConnectionsByLocation (@RequestHeader("Authorization") String token, @PathVariable Long tripId, @RequestParam("x") String x, @RequestParam("y") String y) {
    Trip trip = tripService.getTripById(tripId);
    String end = trip.getMeetUpPlace().getStationCode();
    String start = ConnectionService.getLocationsCoord(x, y).getStationCode();
    return ConnectionService.getConnectionsByCode(start, end);
  }


  @PutMapping("/trips/{tripId}/invitation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void acceptInvitation(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.acceptInvitation(user, trip);
  }
  @DeleteMapping("/trips/{tripId}/invitation")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void rejectInvitation(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.rejectInvitation(user, trip);
  }

  @GetMapping("/trips/{tripId}/admin")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public boolean isAdmin(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    return tripService.isAdmin(tripId, user);
  }
  @PutMapping("/trips/{tripId}/admin/{adminId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void newAdmin(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("adminId") Long adminId) {
    User oldAdmin = userService.getUserByToken(token);
    User newAdmin = userService.getUserById(adminId);
    tripService.newAdmin(tripId, oldAdmin, newAdmin);
  }
  @DeleteMapping("/trips/{tripId}/exit")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void leaveTrip(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.leaveTrip(user, trip);
  }
  @DeleteMapping("/trips/{tripId}/users/{userId}/kick")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void removeMemberFromTrip(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("userId") Long userId) {
    User userToBeRemoved = userService.getUserById(userId);
    User requester = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.removeMemberFromTrip(userToBeRemoved, requester, trip);
  }
  @DeleteMapping("/trips/{tripId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteTrip(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User requester = userService.getUserByToken(token);
    tripService.deleteTrip(tripId, requester);
  }



  @PostMapping("/trips/{tripId}/connection")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void saveConnection(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody List<ConnectionDTO> connectionDTOS) {
    NullChecker.connectionDTOsChecker(connectionDTOS);
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);

    List<ParticipantConnection> connections = new ArrayList<>();
    for (ConnectionDTO dto : connectionDTOS) {
      connections.add(DTOMapper.INSTANCE.convertConnectionDTOToEntity(dto));
    }
    connectionService.saveConnection(participant, connections);
  }
  @PutMapping("/trips/{tripId}/connection")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateConnection(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody List<ConnectionDTO> connectionDTOS) {
    NullChecker.connectionDTOsChecker(connectionDTOS);
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);

    List<ParticipantConnection> connections = new ArrayList<>();
    for (ConnectionDTO dto : connectionDTOS) {
      connections.add(DTOMapper.INSTANCE.convertConnectionDTOToEntity(dto));
    }
    connectionService.udpateConnection(participant, connections);
  }
  @GetMapping("/trips/{tripId}/connection")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
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
  @ResponseBody
  public void deleteConnection(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    TripParticipant participant = tripParticipantService.getTripParticipant(trip, user);
    connectionService.deleteConnection(participant);
  }

  @GetMapping("/trips/{tripId}/connections")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<ConnectionAndUserDTO> getConnections(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    User user = userService.getUserByToken(token);
    Trip trip = tripService.getTripById(tripId);
    tripParticipantService.isPartOfTripAndHasAccepted(user, trip);
    List<TripParticipant> participants = tripParticipantService.getTripParticipants(trip);

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

  @GetMapping("/trips/{tripId}/todos")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public void getTodos(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId) {
    Trip trip = tripService.getTripById(tripId);
    listService.getTodos(trip);
  }

  @PutMapping("trips/{tripId}/todos/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateTodos(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId, @RequestBody ToDoPutDTO toDoPutDTO) {
    Trip trip = tripService.getTripById(tripId);
    ToDoItem toDoItem = DTOMapper.INSTANCE.convertToDoPutDTOToEntity(toDoPutDTO);
    listService.updateTodo(trip, itemId, toDoItem);
  }

  @PostMapping("trips/{tripId}/todos")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public String createTodo(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @RequestBody ToDoPostDTO toDoPostDTO) {
    Trip trip = tripService.getTripById(tripId);
    ToDoItem toDoItem = DTOMapper.INSTANCE.convertToDoPostDTOToEntity(toDoPostDTO);
    return listService.addTodo(trip, toDoItem);
  }

  @DeleteMapping("trips/{tripId}/todos/{itemId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void deleteTodo(@RequestHeader("Authorization") String token, @PathVariable("tripId") Long tripId, @PathVariable("itemId") Long itemId) {
    Trip trip = tripService.getTripById(tripId);
    listService.deleteTodo(trip, itemId);
  }
}
