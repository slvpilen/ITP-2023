package ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.UserScore;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to send HTTP requests to the /highscores endpoint. 
 * The supported operations are GET and POST (get and post highscores).
 */
public class RestRequest {

  private final HttpClient httpClient;
  private final String baseUri;

  /**
   * Constructor for RestRequest. Initializes an HttpClient for easier HTTP requests.

   * @param baseUri The base URI for HTTP requests; typically 'http://localhost:8080' when run
   *                locally.
   */
  public RestRequest(String baseUri) {
    this(baseUri, HttpClient.newHttpClient());
  }

  /**
   * Constructor for RestRequest, used for testing purposes.
   * Allows for a mock HttpClient to be passed in.

   * @param baseUri The base URI for HTTP requests; typically 'http://localhost:8080'
   * @param httpClient The HttpClient used to send HTTP requests.
   */
  public RestRequest(String baseUri, HttpClient httpClient) {
    this.httpClient = httpClient;
    this.baseUri = baseUri;
  }

  /**
   * This method is used to send a GET request to the /highscores endpoint. It reads the highscore
   * file and returns a list of UserScore objects.

   * @return A list of UserScore objects.
   */
  public List<UserScore> readFromHighscore() {
    String endpoint = baseUri + "/highscores";
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint)) 
        // Create a new Uniform Resource Identifier (URI) from a string.
        .header("Content-Type", "application/json")
        // Specify the content type of the request, which is JSON.
        .GET() // Specify that this is a GET request.
        .build(); // Build the request.

    try {
      // Send the request and get the response. We are specifying that the response
      // should be a string.
      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());

      // Convert the response from a JSON string to a list of UserScore objects
      ObjectMapper mapper = new ObjectMapper();
      List<UserScore> userScores = mapper.readValue(response.body(),
          new TypeReference<List<UserScore>>() {});
      return userScores;
    } catch (IOException | InterruptedException e) {
      System.out.println("Connection to server failed!");
      // Return an empty list as default, such that the game can still be played.
      return new ArrayList<UserScore>();
    }
  }

  /**
   * This method is used to send a POST request to the /highscores endpoint. It writes a UserScore
   * object to the highscore file. The method is invoked when a player has won and submitted their
   * name.

   * @param userScore This is the UserScore object which is written to the highscore file.
   */
  public void writeToHighscore(UserScore userScore) {
    String endpoint = baseUri + "/highscores";
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(endpoint))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers
        .ofString(userScore.toJson())) // The restController has a @RequestBody
        // annotation, so we need to specify the body (The userScore) of the request.
        .build();

    try {
      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        // Status code 200 means that the HTTP operation was received, understood and
        // accepted. If the status code is not 200, something unexpected happened.
        System.out.println("Something went wrong when writing to highscore");
      }
    } catch (IOException | InterruptedException e) {
      // You land here if there are network failures, or if there are issues with the server.
      System.out.println("Connection to server failed!");
    }
  }
}
