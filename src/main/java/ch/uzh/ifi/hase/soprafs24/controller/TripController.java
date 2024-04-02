package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.BasicTripInfoGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.MatchingUserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ParticipantGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.TripParticipantService;
import ch.uzh.ifi.hase.soprafs24.service.TripService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class TripController {

  private final TripService tripService;
  private final UserService userService;

  private final TripParticipantService tripParticipantService;



  TripController(TripService tripService, UserService userService, TripParticipantService tripParticipantService) {
    this.tripService = tripService;
    this.userService = userService;
    this.tripParticipantService = tripParticipantService;
  }

  @PostMapping("/trips/new")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void createTrip(@RequestHeader ("Authorization") String token, @RequestBody TripPostDTO tripPostDTO) {
    List<Long> userIds = tripPostDTO.getParticipants();
    String temporaryMeetUpPlace = tripPostDTO.getTemporaryMeetUpPlace();
    String temporaryMeetUpCode = tripPostDTO.getTemporaryMeetUpCode();
    if (temporaryMeetUpCode == null || temporaryMeetUpPlace == null) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "MeetUpCode or MeetUpPlace are null");
    }

    List<User> invited = new ArrayList<>();
    Set<Long> set = new HashSet<>();
    for (Long id : userIds) {
      if (set.add(id)) {
        invited.add(userService.getUserById(id));
      }
    }

    Trip tripInput = DTOMapper.INSTANCE.convertTripPostDTOtoEntity(tripPostDTO);
    User administrator = userService.getUserByToken(token);
    if (userIds.contains(administrator.getId())) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "You invited yourself to the trip");
    }
    tripService.createTrip(tripInput, administrator, invited, temporaryMeetUpPlace, temporaryMeetUpCode);
  }

  @GetMapping("/trips/{tripId}/participants")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<ParticipantGetDTO> getTripParticipants(@RequestHeader("Authorization") String token, @PathVariable Long tripId) {
    Trip trip = tripService.getTripById(tripId);
    List<User> users = tripParticipantService.getTripParticipants(trip);
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
    List<Trip> tripHistory = tripParticipantService.getTripHistory(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (Trip trip : tripHistory) {
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
    List<Trip> tripHistory = tripParticipantService.getCurrentTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (Trip trip : tripHistory) {
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
    List<Trip> tripHistory = tripParticipantService.getFavoriteTrips(user);
    List<BasicTripInfoGetDTO> basics = new ArrayList<>();
    for (Trip trip : tripHistory) {
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
}
