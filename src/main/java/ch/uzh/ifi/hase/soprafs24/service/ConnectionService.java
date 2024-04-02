package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Station;
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
        retStation.setStationCode(jsonStation.getString("id"));
        retStation.setStationName(jsonStation.getString("name"));
        stations.add(retStation);
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
      retStation.setStationCode(jsonStation.getString("id"));
      retStation.setStationName(jsonStation.getString("name"));
      stations.add(retStation);
    }
    return stations;
  } catch(IOException | InterruptedException apiException) {
    throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "There was an error during API request to the external transport api");
  }
}

}
