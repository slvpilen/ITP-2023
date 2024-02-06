package springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.UserScore;
import core.savehandler.HighscoreFileManager;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.io.FileInputStream;
import java.util.List;

/**
 * This class is very similar to the HighscoreRestControllerTest class.
 * The difference is that this class tests the HighscoreService class, which is
 * in contrast to the controller test, where a mock version of HighscoreService
 * is used.
 * 
 * So you can look at this class as an extension of the
 * HighscoreRestControllerTest-class.
 * It is possible that the ControllerTest-class passes all the tests, but the
 * ServiceTest-class fails. This is because the ServiceTest-class tests the
 * actual implementation of the service-methods, while the ControllerTest-class
 * just tests that the HTTP requests are received and that the correct methods
 * for the particular HTTP request are called.
 */
@SpringBootTest(classes = SpringApp.class) // Set the Spring Boot application to be used in the test.
@Import(HighscoreRestController.class) // Set the controller to be used in the test.
@ContextConfiguration(classes = HighscoreService.class) // Set the service to be used in the test.
@AutoConfigureMockMvc // Automatically configure the MockMvc object.
public class HighscoreServiceTest {

  private final String highscorePath = "./../appdata/highscore.json";
  
  // This guy is used to simulate HTTP interactions. It's a mock, so it's not a real server.
  @Autowired
  private MockMvc mockMvc;

  // Instantiating a new field variable of type ObjectMapper with the @Autowired annotation.
  @Autowired
  private ObjectMapper objectMapper; 

  @Test
  public void testGetAllHighscores() throws Exception {

    List<UserScore> highscores = objectMapper.readValue(new FileInputStream(highscorePath),
        new TypeReference<List<UserScore>>() {
        }); // Read in the userScores from the highscore.json file.
    
    MvcResult mockzyRez = mockMvc.perform(get("/highscores")) // Perform a GET request to the /highscores endpoint.
        .andExpect(status().isOk()) // Expect the status code to be 200 (OK).
        .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Expect the content type to be JSON.
        // Check that the response in JSON format is equal to the highscores list
        // written as a JSON string.
        .andExpect(content().json(objectMapper.writeValueAsString(highscores)))
        .andReturn(); // Return the result of the GET request.

    String responseBody = mockzyRez.getResponse().getContentAsString(); // Get the response body as a string.
    
    // Convert the response body from a JSON string to a list of UserScore objects.
    List<UserScore> scory = objectMapper.readValue(responseBody, new TypeReference<List<UserScore>>() {});

    // Check that the highscores list contains all the UserScore objects in the response body.
    assertTrue(scory.stream().filter(score -> highscores.stream()
        .anyMatch(highscore -> highscore.getName().equals(score.getName())
            && highscore.getScore() == score.getScore() && highscore.getDate().equals(score.getDate())
            && highscore.getDifficulty().equals(score.getDifficulty())))
        .count() == highscores.size()); 
  }

  @Test
  public void addHighscore() throws Exception {
    UserScore oskar = new UserScore("oskar", 15, "2023-10-15", "EASY");

    mockMvc.perform(post("/highscores") // Perform a POST request to the /highscores endpoint.
        .contentType(MediaType.APPLICATION_JSON) // Set the content type to JSON.
        .content(objectMapper.writeValueAsString(oskar))) 
        // Set the content of the request to the oskar UserScore object written as a JSON string.
        .andExpect(status().isOk()); // Expect the status code to be 200 (OK).

    List<UserScore> highscores = objectMapper.readValue(new FileInputStream(highscorePath),
        new TypeReference<List<UserScore>>() {
        }); // Read in the userScores from the highscore.json file.

    // Check that the oskar UserScore object is in the highscores list.
    assertTrue(highscores.stream()
        .anyMatch(userScore -> userScore.getName().equals(oskar.getName()) && userScore.getScore() == oskar.getScore()
            && userScore.getDate().equals(oskar.getDate()) && userScore.getDifficulty().equals(oskar.getDifficulty())));

    // Delete oskar from the highscore file, he is only there for testing purposes.
    HighscoreFileManager.deleteFromHighscore(oskar.getName(), oskar.getScore(), oskar.getDate(), oskar.getDifficulty(),
        HighscoreFileManager.getFile());
  }

  @Test
  public void clearAllHighscores() throws Exception {

    // Start by saving the original highscores.
    List<UserScore> highscores = objectMapper.readValue(new FileInputStream(highscorePath),
        new TypeReference<List<UserScore>>() {
        });

    mockMvc.perform(delete("/highscores")) // Perform a DELETE request to the /highscores endpoint.
        .andExpect(status().isOk()); // Expect the status code to be 200 (OK).

    List<UserScore> shouldBeEmpty = objectMapper.readValue(new FileInputStream(highscorePath),
        new TypeReference<List<UserScore>>() {
        }); // Read in the userScores from the highscore.json file.

    // Check that the highscores list is empty.
    assertTrue(shouldBeEmpty.isEmpty());

    // Write the original highscores back to the highscore file.
    highscores.stream()
        .forEach(highscore -> HighscoreFileManager.writeToHighscore(highscore, HighscoreFileManager.getFile()));
  }
}
