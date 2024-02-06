package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import core.UserScore;
import core.settings.SettingsManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class HighScoreListTest extends ApplicationTest {

  private Parent root;
  private FxRobot robot;
  private RestRequest restRequest;

  @Override
  public void start(Stage stage) throws IOException {
    RestRequest mockzy = Mockito.mock(RestRequest.class);
    this.restRequest = mockzy;
    Mockito.when(mockzy.readFromHighscore()).thenReturn(List.of(
        new UserScore("MineLegend", 14, "2021-04-19", "EASY"),
        new UserScore("Christian", 100, "2021-04-20", "MEDIUM"),
        new UserScore("David", 200, "2021-04-21", "HARD"),
        new UserScore("Oskar", 300, "2021-04-22", "MEDIUM"),
        new UserScore("Underdal", 400, "2021-04-23", "HARD")));

    HighscoreListController ctrl = new HighscoreListController();
    ctrl.setRestRequest(mockzy);
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/HighscoreList.fxml"));
    fxmlLoader.setControllerFactory(cls -> ctrl);
    root = fxmlLoader.load();
    stage.setScene(new Scene(root));
    MineApp.setStageSize(stage, HighscoreListController.STAGE_WIDTH, HighscoreListController.STAGE_HEIGHT);
    stage.show();
    robot = new FxRobot();
  }

  /**
   * Clicks on the labels in the fxml-file which has the same name as the string
   * parameters. This method is used to click on buttons, which happen to be
   * a subclass of the Labeled class.
   * 
   * @param labels A list of strings which are the names of the labels you want to
   *               click on.
   */
  private void click(String... labels) {
    for (String label : labels) {
      clickOn(LabeledMatchers.hasText(label));
    }
  }

  /**
   * Checks if the expected player is present in the graphical user interface.
   * 
   * @param userScore The expected player.
   * @param position  The position of the player in the highscore list (1-10)
   */
  private void checkPlayer(UserScore userScore, int position) {
    assertEquals(userScore.getName(), robot.lookup("#name" + position).queryLabeled().getText());
    assertEquals("" + userScore.getScore(), robot.lookup("#score" + position).queryLabeled().getText());
    assertEquals(userScore.getDate(), robot.lookup("#date" + position).queryLabeled().getText());
  }

  @Test
  public void right_order() {
    List<UserScore> userScores = restRequest.readFromHighscore();
    userScores = userScores.stream()
        .filter(score -> score.getDifficulty().equals(SettingsManager.getGameDifficultyAsString()))
        .sorted((a, b) -> a.getScore() - b.getScore())
        .toList();

    for (int i = 1; i < Math.min(11, userScores.size()); i++) {
      checkPlayer(userScores.get(i - 1), i);
    }
  }

  @Test
  public void testBackButton() {
    assertEquals(false, robot.lookup("#gameGrid").tryQuery().isPresent());
    click("Back");
    assertEquals(true, robot.lookup("#gameGrid").tryQuery().isPresent());
  }

  @Test
  public void testChoiceBox() {
    List<UserScore> userScores = restRequest.readFromHighscore();
    
    @SuppressWarnings("unchecked") // We know that the ChoiceBox is of type String.
    ChoiceBox<String> choiceBox = robot.lookup("#difficultyChoiceBox").queryAs(ChoiceBox.class);

    robot.clickOn("#difficultyChoiceBox");
    click("MEDIUM");
    assertEquals("MEDIUM", choiceBox.getValue());
    checkPlayer(userScores.get(1), 1);

    robot.clickOn("#difficultyChoiceBox");
    click("HARD");
    assertEquals("HARD", choiceBox.getValue());
    checkPlayer(userScores.get(2), 1);

    robot.clickOn("#difficultyChoiceBox");
    click("EASY");
    assertEquals("EASY", choiceBox.getValue());
    checkPlayer(userScores.get(0), 1);
  }

  @SuppressWarnings("unchecked") // We know that the ChoiceBox is of type String.
  @Test
  public void testStandardChoiceBox() {
    switchDifficulty("Medium");
    ChoiceBox<String> choiceBox = robot.lookup("#difficultyChoiceBox").queryAs(ChoiceBox.class);
    assertEquals("MEDIUM", choiceBox.getValue());

    switchDifficulty("Hard");
    choiceBox = robot.lookup("#difficultyChoiceBox").queryAs(ChoiceBox.class);
    assertEquals("HARD", choiceBox.getValue());

    switchDifficulty("Easy");
    choiceBox = robot.lookup("#difficultyChoiceBox").queryAs(ChoiceBox.class);
    assertEquals("EASY", choiceBox.getValue());
  }

  private void switchDifficulty(String difficulty) {
    click("Back");
    click("Settings");
    click(difficulty);
    click("Back");

    PrintStream originalOut = System.out;
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    click("Leaderboard");
    System.setOut(originalOut);
  }
}
