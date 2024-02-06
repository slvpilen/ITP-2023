package springboot;

import core.UserScore;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is used to handle HTTP requests. It is a REST controller.
 */
@RestController
public class HighscoreRestController {

  private final HighscoreService highscoreService;

  /**
   * Constructor for HighscoreRestController, which is used to handle HTTP requests.
   * The great thing about Spring is that it automatically creates
   * an instance of HighscoreService. 
   * This means that we don't have to create an instance of HighscoreService ourselves.

   * @param highscoreService This is the class which contains
   *     the methods for handling HTTP requests.
   */
  @Autowired
  public HighscoreRestController(HighscoreService highscoreService) {
    this.highscoreService = highscoreService;
  }

  /**
   * This method is used to handle GET requests to the /highscores endpoint.

   * @return A list of UserScore objects.
   */
  @GetMapping("/highscores")
  public List<UserScore> getAllHighscores() {
    return highscoreService.getAllHighscores();
  }

  /**
   * This method is used to handle POST requests to the /highscores endpoint. Spring Boot is super
   * smart, and automatically converts the JSON string (which is sent in the request body) to a
   * UserScore object.

   * @param userScore This is the UserScore object which is written to the highscore file.
   */
  @PostMapping("/highscores")
  public void addHighscore(@RequestBody UserScore userScore) {
    highscoreService.addHighscore(userScore);
  }

  /**
   * This method is used to handle DELETE requests to the /highscores endpoint. It deletes all the
   * highscores from the highscore file.
   */
  @DeleteMapping("/highscores")
  public void clearAllHighscores() {
    highscoreService.clearAllHighscores();
  }
}