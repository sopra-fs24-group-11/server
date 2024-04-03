package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.ConnectionType;
import ch.uzh.ifi.hase.soprafs24.entity.Station;
import ch.uzh.ifi.hase.soprafs24.entity.Connection;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ConnectionService {
  public static List<Station> getLocationsName(String name) {
    try {
      // Http request to transport.opendata
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(String.format("http://transport.opendata.ch/v1/locations?query=%s&type=station", name)))
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
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "There was an error during API request to the external transport api");
    }
  }

  public static List<Station> getLocationsCoord(String x, String y) {
    try {
      // Http request to transport.opendata
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(String.format("http://transport.opendata.ch/v1/locations?x=%s&y=%s", x, y)))
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
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "There was an error during API request to the external transport api");
    }
  }

  public static List<List<Connection>> getConnectionsByCode(String from, String to) {
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
              .uri(URI.create(String.format("http://transport.opendata.ch/v1/connections?from=%s&to=%s&limit=5", from, to)))
              .version(HttpClient.Version.HTTP_2)
              .GET()
              .build();
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
            switch (jsonType) {
              case "T":
                retType = ConnectionType.TRAM;
                break;
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
            String retName = jsonType + " " + jsonNumber;

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

            // setup of full connection object
            Connection retConnection = new Connection();
            retConnection.setConnectionType(retType);
            retConnection.setConnectionName(retName);
            retConnection.setDepartureTime(retDepartureTime);
            retConnection.setDeparturePoint(retDepartureStation);
            retConnection.setArrivalTime(retArrivalTime);
            retConnection.setArrivalPoint(retArrivalStation);

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
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "There was an error during API request to the external transport api");
    }
  }

  // get connections with coordinates and destination code in trip destination (defined in /trips/new, TripService.getTripById + getMeetUpPlace)
  // get connections with start location code and destination code in trip destination (defined in /trips/new, TripService.getTripById + getMeetUpPlace)


}
