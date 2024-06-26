package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.entity.ParticipantConnection;
import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.entity.Connection;
import ch.uzh.ifi.hase.soprafs24.entity.TripParticipant;
import ch.uzh.ifi.hase.soprafs24.repository.ParticipantConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ConnectionService {
  private final ParticipantConnectionRepository participantConnectionRepository;
  private final NotificationService notificationService;

  @Autowired
  public ConnectionService(@Qualifier("participantConnectionRepository") ParticipantConnectionRepository participantConnectionRepository, NotificationService notificationService) {
    this.participantConnectionRepository = participantConnectionRepository;
    this.notificationService = notificationService;
  }

  public static List<Station> getLocationsName(String name) {
    try {
      // Http request to transport.opendata: encode name for correct API request
      String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(String.format("http://transport.opendata.ch/v1/locations?query=%s&type=station", encodedName)))
              .version(HttpClient.Version.HTTP_2)
              .GET()
              .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      // parse string to JSON object & create empty list of stations
      JSONObject obj = new JSONObject(response.body());
      JSONArray jsonArray = obj.getJSONArray("stations");
      List<Station> stations = new ArrayList<>();

      // extract name and id of stations and add them to the list of stations
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonStation = jsonArray.getJSONObject(i);

        Station retStation = new Station();
        Object nullableId = jsonStation.get("id");
        if (nullableId instanceof String) {
          retStation.setStationCode(jsonStation.getString("id"));
          retStation.setStationName(jsonStation.getString("name"));
          stations.add(retStation);
        }
      }
      return stations;
    } catch(IOException | InterruptedException apiException) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Es wurde keine Station gefunden.");
    }
  }

  public static Station getLocationsCoord(String x, String y) {
    try {
      // Http request to transport.opendata
      String encodedX = URLEncoder.encode(x, StandardCharsets.UTF_8);
      String encodedY = URLEncoder.encode(y, StandardCharsets.UTF_8);
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("http://transport.opendata.ch/v1/locations?x=%s&y=%s", encodedX, encodedY)))
            .version(HttpClient.Version.HTTP_2)
            .GET()
            .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

    // parse string to JSON object & create empty list of stations
      JSONObject obj = new JSONObject(response.body());
      JSONArray jsonArray = obj.getJSONArray("stations");

      // for loop needed since the station at position [0] could be null
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonStation = jsonArray.getJSONObject(i);
        // extract name and id of stations and add them to the list of stations

        Station retStation = new Station();
        if (!jsonStation.isNull("id")) {
          retStation.setStationCode(jsonStation.getString("id"));
          retStation.setStationName(jsonStation.getString("name"));
          return retStation;
        }
      }
    } catch(IOException | InterruptedException apiException) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Keine Station in deiner Nähe gefunden.");
    }
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Keine Station in deiner Nähe gefunden.");
  }

  public static List<List<Connection>> getConnectionsByCode(String from, String to, String dateString, String timeString, boolean isLate) {
    try {
      String encodedFrom = URLEncoder.encode(from, StandardCharsets.UTF_8);
      String encodedTo = URLEncoder.encode(to, StandardCharsets.UTF_8);
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request;
      if (isLate) {
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://transport.opendata.ch/v1/connections?from=%s&to=%s&limit=5", encodedFrom, encodedTo)))
                .version(HttpClient.Version.HTTP_2)
                .GET()
                .build();
      } else {
        request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("http://transport.opendata.ch/v1/connections?from=%s&to=%s&date=%s&time=%s&isArrivalTime=1&limit=5", encodedFrom, encodedTo, dateString, timeString)))
                .version(HttpClient.Version.HTTP_2)
                .GET()
                .build();
      }
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      // parse string to JSON object & create empty list of lists of connections
      JSONObject obj = new JSONObject(response.body());
      JSONArray jsonConnectionsArray = obj.getJSONArray("connections");
      List<List<Connection>> connections = new ArrayList<>();

      //extract
      for (int i = 0; i < jsonConnectionsArray.length(); i++) {
        JSONObject jsonConnection = jsonConnectionsArray.getJSONObject(i);
        JSONArray jsonSectionsArray = jsonConnection.getJSONArray("sections");
        List<Connection> retConnectionsList = new ArrayList<>();

        for (int j = 0; j < jsonSectionsArray.length(); j++) {
          JSONObject jsonSection = jsonSectionsArray.getJSONObject(j);
          if (!jsonSection.isNull("journey")) {
            // setup of connection type
            String jsonType = jsonSection
                    .getJSONObject("journey")
                    .getString("category");
            ConnectionType retType;
            // using switch statement since this is the only occurrence in the code (use lookup table if duplicated)
            switch (jsonType) {
              case "T":
                retType = ConnectionType.TRAM; break;
              case "B":
                retType = ConnectionType.BUS; break;
              case "BAT":
                retType = ConnectionType.SHIP; break;
              case "PB":
                retType = ConnectionType.CABLEWAY; break;
              default:
                retType = ConnectionType.TRAIN; break;
            }

            // setup of connection name
            String jsonNumber = jsonSection
                    .getJSONObject("journey")
                    .getString("number");
            String retName = jsonType + jsonNumber;

            // setup of connection departure time
            String jsonDepartureTime = jsonSection
                    .getJSONObject("departure")
                    .getString("departure");
            String jsonDepartureTimeNoOffset = jsonDepartureTime.split("\\+")[0];
            LocalDateTime retDepartureTime = LocalDateTime.parse(jsonDepartureTimeNoOffset);

            // setup of connection departure station
            String jsonDepartureId = jsonSection
                    .getJSONObject("departure")
                    .getJSONObject("station")
                    .getString("id");
            String jsonDepartureName = jsonSection
                    .getJSONObject("departure")
                    .getJSONObject("station")
                    .getString("name");
            Station retDepartureStation = new Station();
            retDepartureStation.setStationCode(jsonDepartureId);
            retDepartureStation.setStationName(jsonDepartureName);

            // setup of connection departure platform
            String retDeparturePlatform = jsonSection
                    .getJSONObject("departure")
                    .optString("platform", "");

            // setup of connection arrival time
            String jsonArrivalTime = jsonSection
                    .getJSONObject("arrival")
                    .getString("arrival");
            String jsonArrivalTimeNoOffset = jsonArrivalTime.split("\\+")[0];
            LocalDateTime retArrivalTime = LocalDateTime.parse(jsonArrivalTimeNoOffset);

            // setup of connection arrival station
            String jsonArrivalId = jsonSection
                    .getJSONObject("arrival")
                    .getJSONObject("station")
                    .getString("id");
            String jsonArrivalName = jsonSection
                    .getJSONObject("arrival")
                    .getJSONObject("station")
                    .getString("name");
            Station retArrivalStation = new Station();
            retArrivalStation.setStationCode(jsonArrivalId);
            retArrivalStation.setStationName(jsonArrivalName);

            // setup of connection departure platform
            String retArrivalPlatform = jsonSection
                    .getJSONObject("arrival")
                    .optString("platform", "");

            // setup of full connection object
            Connection retConnection = new Connection();
            retConnection.setConnectionType(retType);
            retConnection.setConnectionName(retName);
            retConnection.setDepartureTime(retDepartureTime);
            retConnection.setDeparturePoint(retDepartureStation);
            retConnection.setDeparturePlatform(retDeparturePlatform);
            retConnection.setArrivalTime(retArrivalTime);
            retConnection.setArrivalPoint(retArrivalStation);
            retConnection.setArrivalPlatform(retArrivalPlatform);

            // add new connection to list
            try {
              retConnectionsList.add(retConnection);
            } catch (Error e) {System.out.println(e);}
          }

        }
        try {
          connections.add(retConnectionsList);
        } catch (Error e2) {System.out.println(e2);}
      }
      return connections;

    } catch(IOException | InterruptedException apiException) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Keine Verbindungen gefunden, versuch es später erneut.");
    }
  }






  // 4 methods:
  public List<ParticipantConnection> getConnection(TripParticipant participant) {
    return participantConnectionRepository.findAllByParticipant(participant);
  }

  public void deleteConnection(TripParticipant participant) {
    participantConnectionRepository.deleteAllByParticipant(participant);
    participantConnectionRepository.flush();
  }

  public void saveConnection(TripParticipant participant, List<ParticipantConnection> connections) {
    if(!participantConnectionRepository.findAllByParticipant(participant).isEmpty()) {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Du hast bereits eine Verbindung. Ändere diese.");

    }
    for (ParticipantConnection connection : connections) {
      connection.setParticipant(participant);
    }
    participantConnectionRepository.saveAll(connections);
    participantConnectionRepository.flush();
    notificationService.createTripNotification(participant.getTrip(), String.format("%s hat eine Verbindung ausgewählt", participant.getUser().getUsername()));
  }

  public void updateConnection(TripParticipant participant, List<ParticipantConnection> newConnection) {
    for (ParticipantConnection connection : newConnection) {
      connection.setParticipant(participant);
    }
    deleteConnection(participant);
    participantConnectionRepository.saveAll(newConnection);
    participantConnectionRepository.flush();
  }

  public String getDateString(LocalDateTime localDateTime) {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return localDateTime.format(dateFormatter);
  }

  public String getTimeString(LocalDateTime localDateTime) {
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm"); // very important: use "HH:mm" instead of "hh:mm" for 24h format
    return localDateTime.format(timeFormatter);
  }

}
