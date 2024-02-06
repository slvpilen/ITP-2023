package ui;

import core.UserScore;
import core.settings.SettingsManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * This class is used to control the highscore list page. Upon initialization,
 * it reads the highscore file and displays the top 10 scores.
 * also sets the background color of the page, depending on the theme settings.
 */
public class HighscoreListController {

  private static final int HIGHSCORE_LENGTH = 10;
  public static final int STAGE_WIDTH = 470;
  public static final int STAGE_HEIGHT = 460;
  private RestRequest restRequest = new RestRequest("http://localhost:8080");
  private String[] difficulties = { "EASY", "MEDIUM", "HARD" };
  private List<UserScore> userScores;
  private List<UserScore> scoresToShow;
  private List<Label> names;
  private List<Label> scores;
  private List<Label> dates;

  @FXML
  private Label name1;
  @FXML
  private Label name2;
  @FXML
  private Label name3;
  @FXML
  private Label name4;
  @FXML
  private Label name5;
  @FXML
  private Label name6;
  @FXML
  private Label name7;
  @FXML
  private Label name8;
  @FXML
  private Label name9;
  @FXML
  private Label name10;
  @FXML
  private Label score1;
  @FXML
  private Label score2;
  @FXML
  private Label score3;
  @FXML
  private Label score4;
  @FXML
  private Label score5;
  @FXML
  private Label score6;
  @FXML
  private Label score7;
  @FXML
  private Label score8;
  @FXML
  private Label score9;
  @FXML
  private Label score10;
  @FXML
  private Label date1;
  @FXML
  private Label date2;
  @FXML
  private Label date3;
  @FXML
  private Label date4;
  @FXML
  private Label date5;
  @FXML
  private Label date6;
  @FXML
  private Label date7;
  @FXML
  private Label date8;
  @FXML
  private Label date9;
  @FXML
  private Label date10;
  @FXML
  private AnchorPane anchorPane;
  @FXML
  private Label difficultyLabel;
  @FXML
  private ChoiceBox<String> difficultyChoiceBox;

  /**
   * Initializes the highscore list page.
   * Reads the highscore file and displays the top 10 scores.
   * Also sets the background color of the page, depending on the theme settings.

   * @throws IOException If the FXML file for the game page could not be found.
   */
  @FXML
  public void initialize() {
    userScores = restRequest.readFromHighscore();

    names = new ArrayList<>(Arrays.asList(
        name1, name2, name3, name4, name5, name6, name7, name8, name9, name10));
    scores = new ArrayList<>(Arrays.asList(
        score1, score2, score3, score4, score5, score6, score7, score8, score9, score10));
    dates = new ArrayList<>(Arrays.asList(
        date1, date2, date3, date4, date5, date6, date7, date8, date9, date10));

    anchorPane.setStyle(SettingsManager.getThemeSettings().getBackgroundStyle());
    difficultyChoiceBox.getItems().addAll(difficulties);
    difficultyChoiceBox.setValue(SettingsManager.getGameDifficultyAsString());
    difficultyChoiceBox.setOnAction(event -> switchLeaderboardDifficulty());
    switchLeaderboardDifficulty(); // Show the highscores for the selected difficulty.
  }

  /**
   * Switches to the game page.

   * @param event The event that triggered this method.
   * @throws IOException If the FXML file for the game page could not be found.
   */
  @FXML
  public void switchToGame(ActionEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/GamePage.fxml"));
    Parent root = fxmlLoader.load();
    Node eventSource = (Node) event.getSource();
    Stage stage = (Stage) eventSource.getScene().getWindow();
    stage.setScene(new Scene(root));
    MineApp.setStageSize(stage, SettingsManager.getStageMinWidth(),
        SettingsManager.getStageMinHeight());
    stage.show();
  }

  /**
   * Displays the highscores for the selected difficulty.
   * Difficulty is selected by clicking in the user interface.
   */
  public void switchLeaderboardDifficulty() {
    String difficulty = difficultyChoiceBox.getValue();
    difficultyLabel.setText(difficulty);
    switch (difficulty) {
      case "EASY":
        difficultyLabel.setStyle("-fx-text-fill: green;");
        break;
      case "MEDIUM":
        difficultyLabel.setStyle("-fx-text-fill: orange;");
        break;
      case "HARD":
        difficultyLabel.setStyle("-fx-text-fill: red;");
        break;
      default:
        throw new IllegalStateException("Invalid game difficulty: " + difficulty + "!");
    }

    scoresToShow = userScores.stream()
        .filter(score -> score.getDifficulty().equals(difficulty))
        .toList();

    for (int i = 0; i < HIGHSCORE_LENGTH; i++) {
      if (i < scoresToShow.size()) {
        names.get(i).setText(scoresToShow.get(i).getName());
        scores.get(i).setText("" + scoresToShow.get(i).getScore());
        dates.get(i).setText(scoresToShow.get(i).getDate());
      } else {
        names.get(i).setText("-");
        scores.get(i).setText("-");
        dates.get(i).setText("-");
      }
    }
  }

  /**
   * Sets the RestRequest object for this class.
   * This method is used for testing purposes.
   */
  protected void setRestRequest(RestRequest restRequest) {
    this.restRequest = restRequest;
  }
}
