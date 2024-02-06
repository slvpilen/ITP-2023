package core.settings;

/**
 * SettingsManager keeps track of the current settings of the game.
 * There are two main settings: game difficulty and theme.
 * The game difficulty can be set to EASY, MEDIUM or HARD, and
 * the theme can be set to LIGHT or DARK.
 * 
 * <p>The standard settings are EASY and LIGHT.

 * @see GameDifficulty
 * @see ThemeSettings
 */
public class SettingsManager {

  private static GameDifficulty gameDifficulty = GameDifficulty.EASY;
  private static ThemeSettings themeSettings = ThemeSettings.LIGHT;

  public static void setGameDifficulty(GameDifficulty gameDifficulty) {
    SettingsManager.gameDifficulty = gameDifficulty;
  }

  public static void setThemeSettings(ThemeSettings themeSettings) {
    SettingsManager.themeSettings = themeSettings;
  }

  public static GameDifficulty getGameDifficulty() {
    return gameDifficulty;
  }

  public static ThemeSettings getThemeSettings() {
    return themeSettings;
  }

  public static int getStageMinHeight() {
    return gameDifficulty.getStageMinHeight();
  }

  public static int getStageMinWidth() {
    return gameDifficulty.getStageMinWidth();
  }

  public static int getSquareSize() {
    return gameDifficulty.getSquareSize();
  }

  public static String getGameDifficultyAsString() {
    return gameDifficulty.toString();
  }

}
