package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class NullChecker {
  // also add a type checker??
  public static void userPostDTOChecker (UserPostDTO dto) {
    if (dto.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null");
    }
    if (dto.getUsername() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
    }
    if (dto.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be null");
    }
    if (dto.getBirthday() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be null");
    }
  }
  public static void userLoginPostDTOChecker (UserLoginPostDTO dto) {
    if (dto.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null");
    }
    if (dto.getUsername() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
    }
  }

  public static void userPutDTOChecker (UserPutDTO dto) {
    if (dto.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null");
    }
    if (dto.getUsername() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
    }
    if (dto.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be null");
    }
    if (dto.getBirthday() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be null");
    }
  }

  public static void messagePostDTOChecker (MessagePostDTO dto) {
    if (dto.getMessage() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be null");
    }
  }

  public static void tripPostDTOChecker (TripPostDTO dto) {
    if (dto.getTripName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip name cannot be null");
    }
    if (dto.getTripDescription() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip description cannot be null");
    }
    if (dto.getTemporaryMeetUpPlace() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up place cannot be null");
    }
    if (dto.getTemporaryMeetUpCode() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up code cannot be null");
    }
    if (dto.getMeetUpTime() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up time cannot be null");
    }
    if (dto.getParticipants() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participants list cannot be null");
    }
    List<Long> ids = dto.getParticipants();
    if (ids.contains(null)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ids in participant list cannot be null");
    }

  }

  public static void tripPutDTOChecker (TripPutDTO dto) {
    if (dto.getTripName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip name cannot be null");
    }
    if (dto.getTripDescription() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trip description cannot be null");
    }
    if (dto.getTemporaryMeetUpPlace() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up place cannot be null");
    }
    if (dto.getTemporaryMeetUpCode() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up code cannot be null");
    }
    if (dto.getMeetUpTime() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up time cannot be null");
    }
    if (dto.getParticipants() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participants list cannot be null");
    }
  }

  public static void connectionPostDTOsChecker(List<ConnectionPostDTO> dtos) {
    for(ConnectionPostDTO dto : dtos) {
      if (dto.getConnectionType() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Connection type cannot be null");
      }
      if (dto.getConnectionName() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Connection name cannot be null");
      }
      if (dto.getDepartureTime() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure time cannot be null");
      }
      if (dto.getDeparturePoint() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure point cannot be null");
      }
      if (dto.getDeparturePoint().getStationName() == null || dto.getDeparturePoint().getStationCode() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure station information cannot be null");
      }
      if (dto.getArrivalTime() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival time cannot be null");
      }
      if (dto.getArrivalPoint() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival point cannot be null");
      }
      if (dto.getArrivalPoint().getStationName() == null || dto.getArrivalPoint().getStationCode() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival station information cannot be null");
      }
    }
  }
}
