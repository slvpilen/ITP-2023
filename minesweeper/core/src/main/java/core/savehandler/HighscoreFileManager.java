package core.savehandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.UserScore;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for reading and writing to the highscore file.
 */
public class HighscoreFileManager {

  private static final File highscoreFile = new File("./../appdata/highscore.json");

  /**
   * Private constructor to prevent instantiation, and to make jacoco not complain.
   * (Jacoco complains if there is no test coverage for a constructor)
   */
  private HighscoreFileManager() {
  }

  /**
   * Getter for the highscore file.

   * @return The highscore file.
   */
  public static File getFile() {
    return highscoreFile;
  }

  /**
   * Writes a UserScore to the highscore file. Ensures that the highscore file is sorted by score,
   * lower scores first.

   * @param userScore The score which the player has achieved.
   */
  public static void writeToHighscore(UserScore userScore, File file) {
    List<UserScore> userScores = readFromHighscore(file);
    userScores.add(userScore);
    sortUserScores(userScores);
    writeToFile(userScores, file);
  }

  /**
   * Sorts a list of UserScores by score, lower scores first.

   * @param userScores The list of UserScores which is to be sorted.
   */
  private static void sortUserScores(List<UserScore> userScores) {
    userScores.sort((a, b) -> a.getScore() - b.getScore());
  }

  /**
   * Writes a list of UserScores to the highscore file.

   * @param userScores The scores which are to be written to the highscore file.
   */
  private static void writeToFile(List<UserScore> userScores, File file) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, userScores);
    } catch (IOException e) {
      // It is actually very unlikely that something will go wrong here.
      // Since if you specify a file which does not exist, it will be created.
      e.printStackTrace();
    }
  }

  /**
   * Reads from the highscore file and returns a list of UserScore objects. If the file cannot be
   * read, an empty list will be returned.

   * @param file The file which is to be read.
   * @return A list containing all saved UserScores.
   */
  public static List<UserScore> readFromHighscore(File file) {
    try (InputStream is = new FileInputStream(file)) {
      ObjectMapper jsonReader = new ObjectMapper();
      return jsonReader.readValue(is, new TypeReference<List<UserScore>>() {
      });
    } catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<UserScore>();
    }
  }

  /**
   * Deletes a UserScore from the highscore file.

   * @param name The name of the player
   * @param time The score of the player
   * @param date The date the score was achieved
   * @param gameDifficulty The difficulty of the game
   * @param file The file which is to be read.
   */
  public static void deleteFromHighscore(String name, int time, String date,
      String gameDifficulty, File file) {
    List<UserScore> userScores = readFromHighscore(file);
    userScores = userScores.stream()
        .filter(score -> !score.getName().equals(name)
            || score.getScore() != time || !score.getDate().equals(date)
            || !score.getDifficulty().equals(gameDifficulty))
        .toList();
    writeToFile(userScores, file);
  }

  /**
   * Removes all data from the highscore file.
   */
  public static void clearHighscore(File file) {
    writeToFile(new ArrayList<UserScore>(), file);
  }
}
