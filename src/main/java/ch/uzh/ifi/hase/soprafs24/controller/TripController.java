package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.FriendshipService;
import ch.uzh.ifi.hase.soprafs24.service.TripService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TripController {

  private final TripService tripService;

  TripController(TripService tripService) {
    this.tripService = tripService;
  }
}
