package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class NullChecker {
  // also add a type checker??
  public static void userPostDTOChecker (UserPostDTO dto) {
    if (dto.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be null");
    }
    if (dto.getUsername() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username cannot be null");
    }
    if (dto.getUsername().length() > 30) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username cannot exceed 30 characters.");
    }
    if (dto.getUsername().length() < 2) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username must have at least 2 characters.");
    }
    if (dto.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be null");
    }
    if (dto.getBirthday() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be null");
    }
    if (dto.getBirthday().isAfter(LocalDate.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be in the future");
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
    if (dto.getUsername().length() > 30) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username cannot exceed 30 characters.");
    }
    if (dto.getUsername().length() < 2) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Username must have at least 2 characters.");
    }
    if (dto.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be null");
    }
    if (dto.getBirthday() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be null");
    }
    if (dto.getBirthday().isAfter(LocalDate.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday cannot be in the future");
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
    if (dto.getMeetUpPlace() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up place cannot be null");
    }
    if (dto.getMeetUpPlace().getStationName() == null || dto.getMeetUpPlace().getStationCode() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up station information cannot be null");
    }
    if (dto.getMeetUpTime() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up time cannot be null");
    }
    if (dto.getMeetUpTime().isBefore(LocalDateTime.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up time cannot be in the past");
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
    if (dto.getMeetUpPlace() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up place cannot be null");
    }
    if (dto.getMeetUpPlace().getStationName() == null || dto.getMeetUpPlace().getStationCode() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up station information cannot be null");
    }
    if (dto.getMeetUpTime() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up time cannot be null");
    }
    if (dto.getMeetUpTime().isBefore(LocalDateTime.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meet-up time cannot be in the past");
    }
    if (dto.getParticipants() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participants list cannot be null");
    }
    List<Long> ids = dto.getParticipants();
    if (ids.contains(null)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ids in participant list cannot be null");
    }
  }

  public static void connectionDTOsChecker(List<ConnectionDTO> dtos) {
    for(ConnectionDTO dto : dtos) {
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
  public static void itemPostDTOChecker(ItemPostDTO dto) {
    if (dto.getItem() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item cannot be null");
    }
  }
  public static void templateDTOChecker(TemplateDTO dto) {
    if (dto.getItem() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item cannot be null");
    }
  }
  public static void imageChecker(MultipartFile image) {
    if (image.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload an image");
    }
    String type = image.getContentType();
    if (!Objects.equals(type, "image/png") && !Objects.equals(type, "image/jpeg")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload an image of type png or jpg/jpeg/jpe/jfif");
    }
    long maxSizeInBytes = 3 * 1024 * 1024; // 3 MB (adjust as needed) - 10MB is internal server maximum - we only allow 3 MB
    if (image.getSize() > maxSizeInBytes) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image file size exceeds the maximum allowed size of 3MB.");
    }
  }

}
