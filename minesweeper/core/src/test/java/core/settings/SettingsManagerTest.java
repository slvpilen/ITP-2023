package core.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * This test isn't really necessary, but jacoco complains
 * if we never instantiate the SettingsManager class.
 */
public class SettingsManagerTest {

  @Test
  public void testSettingsManager() {
    SettingsManager settingsManager = new SettingsManager();
    settingsManager.toString(); // Just to get rid of the warning
    assertEquals("EASY", SettingsManager.getGameDifficultyAsString());
    assertEquals("-fx-background-color: white", SettingsManager.getThemeSettings().getBackgroundStyle());
  }
}