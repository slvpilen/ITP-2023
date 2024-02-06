package core.savehandler;

import core.settings.SettingsManager;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import core.UserScore;
import core.settings.GameDifficulty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.util.List;

public class FileManagerTest {

  private static final File testFile = new File("src/test/resources/testHighscore.json");

  @BeforeEach
  public void setup() {
    HighscoreFileManager.clearHighscore(testFile);
  }

  @Test
  public void checkFile() {
    File expectedFile = new File("./../appdata/highscore.json");
    File actualFile = HighscoreFileManager.getFile();
    assertEquals(expectedFile, actualFile, "File is not correct");
  }

  @Test
  public void clearHighscore() throws Exception {
    UserScore bert = new UserScore("Bert", 100, "2020-10-04", SettingsManager.getGameDifficultyAsString());
    UserScore bernard = new UserScore("Bernard", 30, "2021-09-08", SettingsManager.getGameDifficultyAsString());
    UserScore alfred = new UserScore("Alfred", 50, "2021-09-15", SettingsManager.getGameDifficultyAsString());
    HighscoreFileManager.writeToHighscore(bert, testFile);
    HighscoreFileManager.writeToHighscore(bernard, testFile);
    HighscoreFileManager.writeToHighscore(alfred, testFile);

    HighscoreFileManager.clearHighscore(testFile);
    ObjectMapper jsonReader = new ObjectMapper();
    List<UserScore> userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {
    });

    assertEquals(0, userScores.size(), "File is not empty after clearing: " + userScores.toString());
  }

  @Test
  public void writeToEmptyHighscore() throws Exception {
    UserScore pedor = new UserScore("Pedor", 50, "2023-10-04", SettingsManager.getGameDifficultyAsString());
    HighscoreFileManager.writeToHighscore(pedor, testFile);
    ObjectMapper jsonReader = new ObjectMapper();
    List<UserScore> userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {
    });

    assertEquals(1, userScores.size(), userScores.toString());
    assertEquals("Pedor", userScores.get(0).getName(), "Name is not correct");
    assertEquals(50, userScores.get(0).getScore(), "Score is not correct");
    assertEquals("2023-10-04", userScores.get(0).getDate(), "Date is not correct");
  }

  @Test
  public void testHighscoreSorting() throws Exception {
    UserScore alfa = new UserScore("Alfa", 100, "1908-01-04", SettingsManager.getGameDifficultyAsString());
    UserScore beta = new UserScore("Beta", 30, "1945-05-08", SettingsManager.getGameDifficultyAsString());
    UserScore gamma = new UserScore("Gamma", 50, "1940-04-09", SettingsManager.getGameDifficultyAsString());
    UserScore zeta = new UserScore("Zeta", 480, "2021-09-15", SettingsManager.getGameDifficultyAsString());
    HighscoreFileManager.writeToHighscore(alfa, testFile);
    HighscoreFileManager.writeToHighscore(beta, testFile);
    HighscoreFileManager.writeToHighscore(gamma, testFile);
    HighscoreFileManager.writeToHighscore(zeta, testFile);

    ObjectMapper jsonReader = new ObjectMapper();
    List<UserScore> userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {
    });

    assertEquals(4, userScores.size(), "Size is not correct: " + userScores.toString());

    assertEquals("Beta", userScores.get(0).getName(), "Name is not correct");
    assertEquals(30, userScores.get(0).getScore(), "Score is not correct");
    assertEquals("1945-05-08", userScores.get(0).getDate(), "Date is not correct");

    assertEquals("Gamma", userScores.get(1).getName(), "Name is not correct");
    assertEquals(50, userScores.get(1).getScore(), "Score is not correct");
    assertEquals("1940-04-09", userScores.get(1).getDate(), "Date is not correct");

    assertEquals("Alfa", userScores.get(2).getName(), "Name is not correct");
    assertEquals(100, userScores.get(2).getScore(), "Score is not correct");
    assertEquals("1908-01-04", userScores.get(2).getDate(), "Date is not correct");

    assertEquals("Zeta", userScores.get(3).getName(), "Name is not correct");
    assertEquals(480, userScores.get(3).getScore(), "Score is not correct");
    assertEquals("2021-09-15", userScores.get(3).getDate(), "Date is not correct");
  }

  /**
   * If we have come to this test, then we know that writing to highscore works,
   * therefore we can use HighscoreFileManager.writeToFile here..
   * 
   * @throws Exception If the file cannot be read.
   */
  @Test
  public void readFromHighscore() throws Exception {
    UserScore bert = new UserScore("Bert", 100, "2020-10-04", SettingsManager.getGameDifficultyAsString());
    UserScore bernard = new UserScore("Bernard", 200, "2021-09-08", SettingsManager.getGameDifficultyAsString());
    UserScore alfred = new UserScore("Alfred", 300, "2021-09-15", SettingsManager.getGameDifficultyAsString());
    HighscoreFileManager.writeToHighscore(bert, testFile);
    HighscoreFileManager.writeToHighscore(bernard, testFile);
    HighscoreFileManager.writeToHighscore(alfred, testFile);

    List<UserScore> userScores = HighscoreFileManager.readFromHighscore(testFile);

    assertEquals(3, userScores.size(), userScores.toString());

    assertEquals("Bert", userScores.get(0).getName(), "Name is not correct");
    assertEquals(100, userScores.get(0).getScore(), "Score is not correct");
    assertEquals("2020-10-04", userScores.get(0).getDate(), "Date is not correct");

    assertEquals("Bernard", userScores.get(1).getName(), "Name is not correct");
    assertEquals(200, userScores.get(1).getScore(), "Score is not correct");
    assertEquals("2021-09-08", userScores.get(1).getDate(), "Date is not correct");

    assertEquals("Alfred", userScores.get(2).getName(), "Name is not correct");
    assertEquals(300, userScores.get(2).getScore(), "Score is not correct");
    assertEquals("2021-09-15", userScores.get(2).getDate(), "Date is not correct");
  }

  @Test
  public void readFromNonExistingFile() {

    // Setting up a new error stream, such that printstacktrace does not go off when
    // running mvn test.
    PrintStream originalErrorStream = System.err;
    System.setErr(new PrintStream(new OutputStream() {
      public void write(int b) { // Don't write anything.
      }
    }));

    List<UserScore> userScores = HighscoreFileManager
        .readFromHighscore(new File("src/test/resources/nonExistingHighscore.json"));
    assertEquals(0, userScores.size(),
        "File is somehow not empty, even though you have read from a file which does not exist.");

    // Resetting the error stream.
    System.setErr(originalErrorStream);
  }

  @Test
  public void testDeletionNameDifferent() throws Exception {
    String name = "Brenda";
    int score = 24;
    String date = "2023-01-01";

    UserScore brenda1 = new UserScore(name, score, date, SettingsManager.getGameDifficultyAsString());
    UserScore brenda2 = new UserScore("Brenda2", score, date, SettingsManager.getGameDifficultyAsString());
    
    HighscoreFileManager.writeToHighscore(brenda1, testFile);
    HighscoreFileManager.writeToHighscore(brenda2, testFile);

    HighscoreFileManager.deleteFromHighscore(name, score, date, SettingsManager.getGameDifficultyAsString(), testFile);
    
    ObjectMapper jsonReader = new ObjectMapper();
    List<UserScore> userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    
    assertEquals(1, userScores.size(), "One entry should remain after deletion.");
    UserScore remaining = userScores.get(0);
    
    assertEquals("Brenda2", remaining.getName());
    assertEquals(24, remaining.getScore());
    assertEquals(date, remaining.getDate());
    assertEquals(SettingsManager.getGameDifficultyAsString(), remaining.getDifficulty());

    HighscoreFileManager.deleteFromHighscore("Brenda2", score, date, SettingsManager.getGameDifficultyAsString(), testFile);
    userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    assertEquals(0, userScores.size(), "No entries should remain after deletion.");
  }

  @Test
  public void testDeletionScoreDifferent() throws Exception {
    String name = "Brenda";
    int score = 24;
    String date = "2023-01-01";

    UserScore brenda1 = new UserScore(name, score, date, SettingsManager.getGameDifficultyAsString());
    UserScore brenda2 = new UserScore(name, 25, date, SettingsManager.getGameDifficultyAsString());
    
    HighscoreFileManager.writeToHighscore(brenda1, testFile);
    HighscoreFileManager.writeToHighscore(brenda2, testFile);

    HighscoreFileManager.deleteFromHighscore(name, score, date, SettingsManager.getGameDifficultyAsString(), testFile);
    
    ObjectMapper jsonReader = new ObjectMapper();
    List<UserScore> userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    
    assertEquals(1, userScores.size(), "One entry should remain after deletion.");
    UserScore remaining = userScores.get(0);
    
    assertEquals("Brenda", remaining.getName());
    assertEquals(25, remaining.getScore());
    assertEquals(date, remaining.getDate());
    assertEquals(SettingsManager.getGameDifficultyAsString(), remaining.getDifficulty());

    HighscoreFileManager.deleteFromHighscore(name, 25, date, SettingsManager.getGameDifficultyAsString(), testFile);
    userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    assertEquals(0, userScores.size(), "No entries should remain after deletion.");
  }

  @Test
  public void testDeletionDateDifferent() throws Exception {
    String name = "Brenda";
    int score = 24;
    String date = "2023-01-01";

    UserScore brenda1 = new UserScore(name, score, date, SettingsManager.getGameDifficultyAsString());
    UserScore brenda2 = new UserScore(name, score, "2023-01-02", SettingsManager.getGameDifficultyAsString());
    
    HighscoreFileManager.writeToHighscore(brenda1, testFile);
    HighscoreFileManager.writeToHighscore(brenda2, testFile);

    HighscoreFileManager.deleteFromHighscore(name, score, date, SettingsManager.getGameDifficultyAsString(), testFile);
    
    ObjectMapper jsonReader = new ObjectMapper();
    List<UserScore> userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    
    assertEquals(1, userScores.size(), "One entry should remain after deletion.");
    UserScore remaining = userScores.get(0);
    
    assertEquals("Brenda", remaining.getName());
    assertEquals(24, remaining.getScore());
    assertEquals("2023-01-02", remaining.getDate());

    HighscoreFileManager.deleteFromHighscore(name, score, "2023-01-02", SettingsManager.getGameDifficultyAsString(), testFile);
    userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    assertEquals(0, userScores.size(), "No entries should remain after deletion.");
  }


  @Test
  public void testDeletionDifficultyDifferent() throws Exception {
    String name = "Brenda";
    int score = 24;
    String date = "2023-01-01";

    UserScore brenda1 = new UserScore(name, score, date, SettingsManager.getGameDifficultyAsString());
    SettingsManager.setGameDifficulty(GameDifficulty.MEDIUM);
    UserScore brenda2 = new UserScore(name, score, date, SettingsManager.getGameDifficultyAsString());
    
    HighscoreFileManager.writeToHighscore(brenda1, testFile);
    HighscoreFileManager.writeToHighscore(brenda2, testFile);

    HighscoreFileManager.deleteFromHighscore(name, score, date, SettingsManager.getGameDifficultyAsString(), testFile);
    
    ObjectMapper jsonReader = new ObjectMapper();
    List<UserScore> userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    
    assertEquals(1, userScores.size(), "One entry should remain after deletion.");
    UserScore remaining = userScores.get(0);
    
    assertEquals("Brenda", remaining.getName());
    assertEquals(24, remaining.getScore());
    assertEquals(date, remaining.getDate());
    assertEquals("EASY", remaining.getDifficulty());
    SettingsManager.setGameDifficulty(GameDifficulty.EASY);
    HighscoreFileManager.deleteFromHighscore(name, score, date, SettingsManager.getGameDifficultyAsString(), testFile);

    userScores = jsonReader.readValue(testFile, new TypeReference<List<UserScore>>() {});
    assertEquals(0, userScores.size(), "No entries should remain after deletion.");
  }

  @Test
  public void testWriteToFileIOException() {
    
    File file = new File("src/test/resources/readOnlyHighscore.json");
    file.setReadOnly();
    assertFalse(file.canWrite(), "File should be read only.");

    PrintStream orgError = System.err; // We don't want to print stacktrace to the console.
    ByteArrayOutputStream errorText = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errorText));

    UserScore guttorm = new UserScore("Guttorm", 50, "2021-09-15", SettingsManager.getGameDifficultyAsString());
    
    // An IOException should be thrown, but we are not interested in the stacktrace.
    HighscoreFileManager.writeToHighscore(guttorm, file);
    System.setErr(orgError);

    List<UserScore> userScores = HighscoreFileManager.readFromHighscore(file);
    assertEquals(0, userScores.size()
        , "File was empty before writing, and should be empty after writing, since an IOException was thrown.");
  }
}
