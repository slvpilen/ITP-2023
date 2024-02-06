package ui;

import core.settings.GameDifficulty;
import core.settings.SettingsManager;
import core.settings.ThemeSettings;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

/**
 * Controller for the settings page.
 */
public class SettingsController {
  @FXML
  private Button easyButton;
  @FXML
  private Button mediumButton;
  @FXML
  private Button hardButton;
  @FXML
  private Button darkModeButton;
  @FXML
  private Button lightModeButton;
  @FXML
  private Label themeLabel;
  @FXML
  private Label difficultyLevelLabel;
  @FXML
  private VBox vbox;
  @FXML
  private Line line;

  public static final int STAGE_WIDTH = 600;
  public static final int STAGE_HEIGHT = 500;

  /**
   * Initializes the settings page.
   * The buttons you are able to click are disabled/enabled
   * depending on the current settings.
   */
  @FXML
  public void initialize() {
    switch (SettingsManager.getGameDifficulty()) {
      case EASY:
        setEasy();
        break;
      case MEDIUM:
        setMedium();
        break;
      case HARD:
        setHard();
        break;
      default:
        throw new AssertionError("Unknown difficulty level: "
            + SettingsManager.getGameDifficulty());
    }

    switch (SettingsManager.getThemeSettings()) {
      case LIGHT:
        setLightMode();
        break;
      case DARK:
        setDarkMode();
        break;
      default:
        throw new AssertionError("Unknown theme: " + SettingsManager.getThemeSettings());
    }
  }

  /**
   * Sets the difficulty level to easy.
   */
  @FXML
  public void setEasy() {
    difficultyLevelLabel.setText("Easy");
    difficultyLevelLabel.setTextFill(Paint.valueOf("green"));
    easyButton.setDisable(true);
    mediumButton.setDisable(false);
    hardButton.setDisable(false);
    SettingsManager.setGameDifficulty(GameDifficulty.EASY);
  }

  /**
   * Sets the difficulty level to medium.
   */
  @FXML
  public void setMedium() {
    difficultyLevelLabel.setText("Medium");
    difficultyLevelLabel.setTextFill(Paint.valueOf("orange"));
    easyButton.setDisable(false);
    mediumButton.setDisable(true);
    hardButton.setDisable(false);
    SettingsManager.setGameDifficulty(GameDifficulty.MEDIUM);
  }

  /**
   * Sets the difficulty level to hard.
   */
  @FXML
  public void setHard() {
    difficultyLevelLabel.setText("Hard");
    difficultyLevelLabel.setTextFill(Paint.valueOf("red"));
    easyButton.setDisable(false);
    mediumButton.setDisable(false);
    hardButton.setDisable(true);
    SettingsManager.setGameDifficulty(GameDifficulty.HARD);
  }

  /**
   * Switches to light mode.
   */
  @FXML
  public void setLightMode() {
    SettingsManager.setThemeSettings(ThemeSettings.LIGHT);
    themeLabel.setText("Light mode");
    themeLabel.setStyle("-fx-font-weight: normal");
    vbox.setStyle(SettingsManager.getThemeSettings().getBackgroundStyle());
    lightModeButton.setDisable(true);
    darkModeButton.setDisable(false);
  }

  /**
   * Switches to dark mode.
   */
  @FXML
  public void setDarkMode() {
    SettingsManager.setThemeSettings(ThemeSettings.DARK);
    themeLabel.setText("Dark mode");
    themeLabel.setStyle("-fx-font-weight: bold");
    vbox.setStyle(SettingsManager.getThemeSettings().getBackgroundStyle());
    lightModeButton.setDisable(false);
    darkModeButton.setDisable(true);
  }

  /**
   * Switches to the game page.

   * @param event The event that triggered this method
   * @throws IOException If the FXML file cannot be loaded
   */
  @FXML
  public void switchToGame(ActionEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui/GamePage.fxml"));
    Parent root = fxmlLoader.load();
    Node eventSource = (Node) event.getSource();
    Stage stage = (Stage) eventSource.getScene().getWindow();
    stage.setScene(new Scene(root));

    MineApp.setStageSize(stage, SettingsManager.getStageMinWidth(),
        SettingsManager.getStageMinHeight());
    stage.show();
  }
}