package ui;

import core.UserScore;
import core.savehandler.HighscoreFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class is used to test the RestRequest class.
 * We are not actually running a server when we run these tests.
 * Instead, we are mocking the server response.
 * 
 * <p>The basic idea is that we want to check that the RestRequest class is able to parse the
 * response from the server correctly, given that the server is running and is returning
 * normal responses.
 */
public class RestRequestTest {

    @Mock
    private HttpClient mockHttpClient = mock(HttpClient.class);

    private RestRequest restRequest;

    @BeforeEach
    public void setup() {
        restRequest = new RestRequest("http://localhost:8080", mockHttpClient);
    }

  /**
   * This test checks that the RestRequest class is able to send a GET request to the server,
   * and that it is able to parse the response correctly.

   * @throws IOException
   * @throws InterruptedException
   */
  @SuppressWarnings("unchecked") 
  @Test
  public void testReadFromHighscore() throws IOException, InterruptedException {
    
    // We are mocking the response from the server here.
    // The server responds by saying that the entire highscore list is David,
    // with a score of 4 on the EASY difficulty.
    UserScore david = new UserScore("David", 4, "2024-01-01", "EASY");
    String body = "[" + david.toJson() + "]";
    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.body()).thenReturn(body);
    when(mockResponse.statusCode()).thenReturn(200);
    
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(mockResponse);

    // We are now sending a restRequest, where the response from server is mocked.
    // We are expecting restRequest to understand the response and return a list of UserScore.
    List<UserScore> result = restRequest.readFromHighscore();
    assertEquals(1, result.size(), "The list should contain one UserScore object.");
    assertEquals("David", result.get(0).getName(), "The name should be David.");
    assertEquals(4, result.get(0).getScore(), "The score should be 4.");
    assertEquals("2024-01-01", result.get(0).getDate(), "The date should be 2024-01-01.");
    assertEquals("EASY", result.get(0).getDifficulty(), "The difficulty should be EASY.");
  }

  /**
   * This test checks that the RestRequest class is able to handle exceptions when reading from
   * the highscore list. We want to make sure that the game is still playable even though the
   * server is not running. Therefore, we are expecting the RestRequest class to return an empty
   * list when the server is not running.

   * @throws IOException 
   * @throws InterruptedException
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testExceptionWhenReadingFromHighscore() throws IOException, InterruptedException {

    // We don't want to print the error message to the console.
    PrintStream orgOut = System.out;
    ByteArrayOutputStream newOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(newOut));

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Could not connect to server."));
    List<UserScore> result = restRequest.readFromHighscore();
    assertEquals(0, result.size(),
        "Even though the server is not running, the game should still be playable.");

    when (mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new InterruptedException("The request was interrupted."));
    result = restRequest.readFromHighscore();
    assertEquals(0, result.size(),
        "Even though the server is not running, the game should still be playable.");
    
    System.setOut(orgOut);
  }

  /**
   * This test checks that the RestRequest class is able to send a POST request to the server.

   * @throws IOException
   * @throws InterruptedException
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testWriteToHighscore() throws IOException, InterruptedException {
    File testHighscore = new File("src/test/resources/highscoreRestTest.json");
    UserScore oskar = new UserScore("Oskar", 5, "2023-11-08", "EASY");

    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(200);
    Mockito.doAnswer(inv -> {
      HighscoreFileManager.writeToHighscore(oskar, testHighscore);
      return mockResponse;
    })
    .when(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    restRequest.writeToHighscore(oskar);
    List<UserScore> result = HighscoreFileManager.readFromHighscore(testHighscore);
    
    assertEquals(2, result.size(), "The highscore list should now contain two UserScore objects.");
    
    assertEquals("David", result.get(0).getName(), "The name should be David.");
    assertEquals(4, result.get(0).getScore(), "The score should be 4.");
    assertEquals("2024-01-01", result.get(0).getDate(), "The date should be 2024-01-01.");
    assertEquals("EASY", result.get(0).getDifficulty(), "The difficulty should be EASY.");

    assertEquals("Oskar", result.get(1).getName(), "The name should be Oskar.");
    assertEquals(5, result.get(1).getScore(), "The score should be 5.");
    assertEquals("2023-11-08", result.get(1).getDate(), "The date should be 2023-11-08.");
    assertEquals("EASY", result.get(1).getDifficulty(), "The difficulty should be EASY.");

    // Delete Oskar from the testHighscore, such that the test is reproducible.
    HighscoreFileManager.deleteFromHighscore("Oskar", 5, "2023-11-08", "EASY", testHighscore);
  }

  /**
   * This test checks that the RestRequest class is able to handle the situation
   * where the server gets the request, but is unable to write to the highscore list.
   * We are expecting the RestRequest class to print an error message to the console.

   * @throws IOException 
   * @throws InterruptedException
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testStatusCodeNot200WhenWriting() throws IOException, InterruptedException{
    UserScore oskar = new UserScore("Oskar", 5, "2023-11-08", "EASY");

    HttpResponse<String> mockResponse = (HttpResponse<String>) mock(HttpResponse.class);
    when(mockResponse.statusCode()).thenReturn(400); // Bad request, the server does not understand the request.

    Mockito.doAnswer(inv -> {
      return mockResponse;
    })
    .when(mockHttpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

    PrintStream orgOut = System.out;
    ByteArrayOutputStream newOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(newOut));
    
    restRequest.writeToHighscore(oskar);
    assertEquals("Something went wrong when writing to highscore", newOut.toString().strip());
    System.setOut(orgOut);
  }

  /**
   * This test checks that the RestRequest class is able to handle the situation where the connection
   * to the server fails. We are expecting the RestRequest class to print an error message to the
   * console.

   * @throws IOException
   * @throws InterruptedException
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testExceptionWhenWritingToHighscore() throws IOException, InterruptedException {
    UserScore oskar = new UserScore("Oskar", 5, "2023-11-08", "EASY");
    
    PrintStream orgOut = System.out;
    ByteArrayOutputStream newOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(newOut));
   
    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Could not connect to server."));
    
    restRequest.writeToHighscore(oskar);
    assertEquals("Connection to server failed!", newOut.toString().strip());
    newOut.reset();

    when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new InterruptedException("The request was interrupted."));
    restRequest.writeToHighscore(oskar);
    assertEquals("Connection to server failed!", newOut.toString().strip());

    System.setOut(orgOut);
  }

  @Test
  public void testConstructor() {
    new RestRequest("http://localhost:8080");
  }
}
