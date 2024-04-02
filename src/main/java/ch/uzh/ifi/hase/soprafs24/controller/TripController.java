package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Trip;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.TripPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.TripService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class TripController {

  private final TripService tripService;
  private final UserService userService;

  TripController(TripService tripService, UserService userService) {
    this.tripService = tripService;
    this.userService = userService;
  }

  @PostMapping("/trips/new")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public void createTrip(@RequestHeader ("Authorization") String token, @RequestBody TripPostDTO tripPostDTO) {
    List<Long> userIds = tripPostDTO.getParticipants();
    String temporaryMeetUpPlace = tripPostDTO.getTemporaryMeetUpPlace();
    String temporaryMeetUpCode = tripPostDTO.getTemporaryMeetUpCode();

    // add check to see if userIds exist and if userIds are not double?
    List<User> invited = new ArrayList<>();
    for (Long id : userIds) {
      invited.add(userService.getUserById(id));
    }


    Trip tripInput = DTOMapper.INSTANCE.convertTripPostDTOtoEntity(tripPostDTO);
    User administrator = userService.getUserByToken(token);
    tripService.createTrip(tripInput, administrator, invited, temporaryMeetUpPlace, temporaryMeetUpCode);
  }
}
