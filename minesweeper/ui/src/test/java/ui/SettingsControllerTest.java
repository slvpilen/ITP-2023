package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import core.settings.GameDifficulty;
import core.settings.SettingsManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class SettingsControllerTest extends ApplicationTest {

    private Parent root;
    private FxRobot robot;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/Settings.fxml"));
        root = fxmlLoader.load();
        stage.setScene(new Scene(root));
        MineApp.setStageSize(stage, SettingsController.STAGE_WIDTH, SettingsController.STAGE_HEIGHT);
        stage.show();
        robot = new FxRobot();
    }

    /**
     * Clicks on the labels in the fxml-file which has the same name as the string
     * parameters.
     * 
     * @param labels A list of strings which are the names of the labels you want to
     *               click on.
     */
    private void click(String... labels) {
        // The button class is a sublass of the Labeled class.
        // So when searching for matching labels, we will also find buttons.
        for (String label : labels) {
            clickOn(LabeledMatchers.hasText(label));
        }
    }

    @Test
    public void testBackButton() {
        assertEquals(false, robot.lookup("#gameGrid").tryQuery().isPresent());
        click("Back");
        assertEquals(true, robot.lookup("#gameGrid").tryQuery().isPresent());
    }

    @Test
    public void testSetMedium() {
        clickOn(robot.lookup("#mediumButton").queryButton());
        assertEquals("Medium", robot.lookup("#difficultyLevelLabel").queryLabeled().getText());
        assertEquals(Paint.valueOf("orange"), robot.lookup("#difficultyLevelLabel").queryLabeled().getTextFill());
        assertEquals(false, robot.lookup("#easyButton").query().isDisabled());
        assertEquals(true, robot.lookup("#mediumButton").query().isDisabled());
        assertEquals(false, robot.lookup("#hardButton").query().isDisabled());
        assertTrue((GameDifficulty.MEDIUM == SettingsManager.getGameDifficulty()));
    }

    @Test
    public void testSetHard() {
        clickOn(robot.lookup("#hardButton").queryButton());
        assertEquals("Hard", robot.lookup("#difficultyLevelLabel").queryLabeled().getText());
        assertEquals(Paint.valueOf("red"), robot.lookup("#difficultyLevelLabel").queryLabeled().getTextFill());
        assertEquals(false, robot.lookup("#easyButton").query().isDisabled());
        assertEquals(false, robot.lookup("#mediumButton").query().isDisabled());
        assertEquals(true, robot.lookup("#hardButton").query().isDisabled());
        assertTrue((GameDifficulty.HARD == SettingsManager.getGameDifficulty()));
    }

    @Test
    public void testSetEasy() {
        clickOn(robot.lookup("#easyButton").queryButton());
        assertEquals("Easy", robot.lookup("#difficultyLevelLabel").queryLabeled().getText());
        assertEquals(Paint.valueOf("green"), robot.lookup("#difficultyLevelLabel").queryLabeled().getTextFill());
        assertEquals(true, robot.lookup("#easyButton").query().isDisabled());
        assertEquals(false, robot.lookup("#mediumButton").query().isDisabled());
        assertEquals(false, robot.lookup("#hardButton").query().isDisabled());
        assertTrue((GameDifficulty.EASY == SettingsManager.getGameDifficulty()));
    }

    @Test
    public void testSetDarkMode() {
        clickOn(robot.lookup("#darkModeButton").queryButton());
        assertEquals("Dark mode", robot.lookup("#themeLabel").queryLabeled().getText());
        assertEquals("-fx-font-weight: bold", robot.lookup("#themeLabel").queryLabeled().getStyle());
        assertEquals(true,
                (SettingsManager.getThemeSettings().getBackgroundStyle() == robot.lookup("#vbox").query().getStyle()));
        assertEquals(false, robot.lookup("#lightModeButton").query().isDisabled());
        assertEquals(true, robot.lookup("#darkModeButton").query().isDisabled());
    }

    @Test
    public void testSetLightMode() {
        clickOn(robot.lookup("#lightModeButton").queryButton());
        assertEquals("Light mode", robot.lookup("#themeLabel").queryLabeled().getText());
        assertEquals("-fx-font-weight: normal", robot.lookup("#themeLabel").queryLabeled().getStyle());
        assertEquals(true,
                (SettingsManager.getThemeSettings().getBackgroundStyle() == robot.lookup("#vbox").query().getStyle()));
        assertEquals(true, robot.lookup("#lightModeButton").query().isDisabled());
        assertEquals(false, robot.lookup("#darkModeButton").query().isDisabled());
    }

}
