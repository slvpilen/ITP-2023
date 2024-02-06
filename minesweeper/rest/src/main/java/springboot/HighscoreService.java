package springboot;

import core.UserScore;
import core.savehandler.HighscoreFileManager;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * This class is used to get appropriate responses to HTTP requests.
 * The controller sends the requests to this class, which then
 * returns the appropriate response.
 */
@Service
public class HighscoreService {

  public List<UserScore> getAllHighscores() {
    return HighscoreFileManager.readFromHighscore(HighscoreFileManager.getFile());
  }

  public void addHighscore(UserScore userScore) {
    HighscoreFileManager.writeToHighscore(userScore, HighscoreFileManager.getFile());
  }

  public void clearAllHighscores() {
    HighscoreFileManager.clearHighscore(HighscoreFileManager.getFile());
  }
}