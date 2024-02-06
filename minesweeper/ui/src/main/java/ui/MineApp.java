package ui;

import core.settings.SettingsManager;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX App.
 */
public class MineApp extends Application {
  /**
   * Initializing the game scene, setting the icon, and starts to play music.
   */
  @Override
  public void start(Stage stage) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/GamePage.fxml"));
    Parent parent = fxmlLoader.load();
    stage.setScene(new Scene(parent));
    setStageSize(stage, SettingsManager.getStageMinWidth(), SettingsManager.getStageMinHeight());
    Image icon = new Image(getClass().getResourceAsStream("/images/truls.jpg"));
    stage.getIcons().add(icon);
    stage.show();
  }

  /**
   * This method is used by all ui classes to set the size of the stage.
   * We have as a policy which states that the loaded
   * stage size must be the minimum size of the stage.

   * @param stage The stage to set the size of.
   * @param width The width of the stage.
   * @param height The height of the stage.
   */
  public static void setStageSize(Stage stage, int width, int height) {
    stage.setMinWidth(width);
    stage.setMinHeight(height);
    stage.setWidth(width);
    stage.setHeight(height);
  }

  public static void main(String[] args) {
    launch();
  }
}