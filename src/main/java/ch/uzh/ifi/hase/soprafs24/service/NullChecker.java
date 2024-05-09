package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class NullChecker {
  private NullChecker() {
    throw new IllegalStateException("Static class");
  }
  public static void userPostDTOChecker (UserPostDTO dto) {
    if (dto.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwort kann nicht null sein.");
    }
    if (dto.getPassword2() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bestätigungspasswort kann nicht null sein.");
    }
    if(dto.getPassword().length() < 4) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwort ist zu kurz. Mindestlänge ist 4 Zeichen.");
    }
    if (!Objects.equals(dto.getPassword(), dto.getPassword2())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwörter stimmen nicht überein.");
    }
    if (dto.getUsername() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Benutzername kann nicht null sein.");
    }
    if (dto.getUsername().length() > 30) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzername kann nicht länger als 30 Zeichen sein.");
    }
    if (dto.getUsername().length() < 2) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzername muss mindestens 2 Zeichen lang haben.");
    }
    if (dto.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-Mail kann nicht null sein.");
    }
    if (!EmailValidator.getInstance().isValid(dto.getEmail())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültige E-Mail Addresse.");
    }
    if (dto.getBirthday() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geburtsdatum kann nicht null sein.");
    }
    if (dto.getBirthday().isAfter(LocalDate.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geburtsdatum kann nicht in der Zukunft liegen.");
    }
  }
  public static void userLoginPostDTOChecker (UserLoginPostDTO dto) {
    if (dto.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwort kann nicht null sein.");
    }
    if (dto.getUsername() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Benutzername kann nicht null sein.");
    }
  }

  public static void userPutDTOChecker (UserPutDTO dto) {
    if (dto.getUsername() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Benutzername kann nicht null sein.");
    }
    if (dto.getUsername().length() > 30) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzername kann nicht länger als 30 Zeichen sein.");
    }
    if (dto.getUsername().length() < 2) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzername muss mindestens 2 Zeichen lang haben.");
    }
    if (dto.getEmail() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "E-Mail kann nicht null sein.");
    }
    if (!EmailValidator.getInstance().isValid(dto.getEmail())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültige E-Mail Addresse.");
    }
    if (dto.getBirthday() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geburtsdatum kann nicht null sein.");
    }
    if (dto.getBirthday().isAfter(LocalDate.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geburtsdatum kann nicht in der Zukunft liegen.");
    }
  }

  public static void passwordPutDTOChecker (PasswordPutDTO dto) {
    if (dto.getPassword() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwort kann nicht null sein.");
    }
    if (dto.getPassword2() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bestätigungspasswort kann nicht null sein.");
    }
    if(dto.getPassword().length() < 4) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwort ist zu kurz. Mindestlänge ist 4 Zeichen.");
    }
    if (!Objects.equals(dto.getPassword(), dto.getPassword2())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die neuen Passwörter stimmen nicht überein.");
    }
  }

  public static void messagePostDTOChecker (MessagePostDTO dto) {
    if (dto.getMessage() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message kann nicht null sein.");
    }
  }

  public static void tripPostDTOChecker (TripPostDTO dto) {
    if (dto.getTripName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise kann nicht null sein.");
    }
    if (dto.getTripName().length() < 2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise muss mindestens 2 Zeichen lang sein.");
    }
    if (dto.getTripName().length() > 20) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise darf höchstens 20 Zeichen lang sein.");
    }
    if (dto.getTripName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise kann nicht nur aus Leerschlägen bestehen.");
    }
    if (dto.getTripDescription() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise kann nicht null sein.");
    }
    if (dto.getTripDescription().length() < 2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise muss mindestens 2 Zeichen lang sein.");
    }
    if (dto.getTripDescription().length() > 200) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise darf höchstens 200 Zeichen lang sein.");
    }
    if (dto.getTripDescription().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise kann nicht nur aus Leerschlägen bestehen.");
    }
    if (dto.getMeetUpPlace() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Treffpunkt kann nicht null sein.");
    }
    if (dto.getMeetUpPlace().getStationName() == null || dto.getMeetUpPlace().getStationCode() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Treffpunkt kann nicht null sein.");
    }
    if (Objects.equals(dto.getMeetUpPlace().getStationName(), "") || Objects.equals(dto.getMeetUpPlace().getStationCode(), "")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wähle eine Station aus.");
    }
    if (dto.getMeetUpTime() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Zeit des Treffpunkts kann nicht null sein.");
    }
    if (dto.getMeetUpTime().isBefore(LocalDateTime.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Zeit des Treffpunkts kann nicht in der Vergangenheit liegen.");
    }
    if (dto.getParticipants() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Liste der Mitglieder kann nicht null sein.");
    }
    List<Long> ids = dto.getParticipants();
    if (ids.contains(null)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Ids in der Mitgliederliste dürfen nicht null sein.");
    }

  }

  public static void tripPutDTOChecker (TripPutDTO dto) {
    if (dto.getTripName() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise kann nicht null sein.");
    }
    if (dto.getTripName().length() < 2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise muss mindestens 2 Zeichen lang sein.");
    }
    if (dto.getTripName().length() > 20) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise darf höchstens 20 Zeichen lang sein.");
    }
    if (dto.getTripName().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Name der Reise kann nicht nur aus Leerschlägen bestehen.");
    }
    if (dto.getTripDescription() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise kann nicht null sein.");
    }
    if (dto.getTripDescription().length() < 2) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise muss mindestens 2 Zeichen lang sein.");
    }
    if (dto.getTripDescription().length() > 200) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise darf höchstens 200 Zeichen lang sein.");
    }
    if (dto.getTripDescription().isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Beschreibung der Reise kann nicht nur aus Leerschlägen bestehen.");
    }
    if (dto.getMeetUpPlace() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Treffpunkt kann nicht null sein.");
    }
    if (dto.getMeetUpPlace().getStationName() == null || dto.getMeetUpPlace().getStationCode() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Treffpunkt kann nicht null sein.");
    }
    if (Objects.equals(dto.getMeetUpPlace().getStationName(), "") || Objects.equals(dto.getMeetUpPlace().getStationCode(), "")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wähle eine Station aus.");
    }
    if (dto.getMeetUpTime() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Zeit des Treffpunkts kann nicht null sein.");
    }
    if (dto.getMeetUpTime().isBefore(LocalDateTime.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Zeit des Treffpunkts kann nicht in der Vergangenheit liegen.");
    }
    if (dto.getParticipants() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Liste der Mitglieder kann nicht null sein.");
    }
    List<Long> ids = dto.getParticipants();
    if (ids.contains(null)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Die Ids in der Mitgliederliste dürfen nicht null sein.");
    }
  }

  public static void connectionDTOsChecker(List<ConnectionDTO> dtos) {
    for(ConnectionDTO dto : dtos) {
      if (dto.getConnectionType() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Verbindungstyp kann nicht null sein.");
      }
      if (dto.getConnectionName() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Der Verbindungsname kann nicht null sein.");
      }
      if (dto.getDepartureTime() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Abfahrtszeit kann nicht null sein.");
      }
      if (dto.getDeparturePoint() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Abfahrtsort kann nicht null sein.");
      }
      if (dto.getDeparturePoint().getStationName() == null || dto.getDeparturePoint().getStationCode() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Abfahrtsstation kann nicht null sein.");
      }
      if (dto.getArrivalTime() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ankunftszeit kann nicht null sein.");
      }
      if (dto.getArrivalPoint() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ankunftsort kann nicht null sein.");
      }
      if (dto.getArrivalPoint().getStationName() == null || dto.getArrivalPoint().getStationCode() == null) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ankunftsstation kann nicht null sein..");
      }
    }
  }
  public static void itemPostDTOChecker(ItemPostDTO dto) {
    if (dto.getItem() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Das Item kann nicht null sein.");
    }
    if (dto.getItem().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Das Item ist zu kurz. Die Minimallänge beträgt 1 Zeichen.");
    }
    if (dto.getItem().length() > 100) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Das Item ist zu lange. Die Maximallänge beträgt 100 Zeichen.");
    }
  }
  public static void templateDTOChecker(TemplateDTO dto) {
    if (dto.getItem() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Das Item kann nicht null sein.");
    }
  }
  public static void imageChecker(MultipartFile image) {
    if (image.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kein Bild ausgewählt.");
    }
    String type = image.getContentType();
    if (!Objects.equals(type, "image/png") && !Objects.equals(type, "image/jpeg")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lade ein Bild vom Typ png oder jpg/jpeg/jpe/jfif hoch.");
    }
    long maxSizeInBytes = (long)3 * 1024 * 1024; // 3 MB (adjust as needed) - 10MB is internal server maximum - we only allow 3 MB
    if (image.getSize() > maxSizeInBytes) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bildgrösse überschreitet die maximal erlaubte Grösse von 3MB.");
    }
  }

}
