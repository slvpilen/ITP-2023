package core.settings;

/**
 * This enum is used to store the different game difficulties which the user can
 * choose from. Currently, there are three difficulties which you can choose
 * from when starting a new game: EASY, MEDIUM and HARD.
 * The test difficulty is used for testing purposes only.
 * 
 * <p>Each difficulty has a different board size, number of bombs, and a minimum stage size.
 * We also have a square size, which is used to determine the size of the tiles, but 
 * currently it is the same for all difficulties.
 * 
 * <p>The good thing about this enum is that it becomes easy to both optimize current difficulties,
 * and add new difficulties. For example, if we wanted to add a new difficulty called
 * "EXTREME", we would only have to add a new enum value, and then set the appropriate
 * values for the board size, number of bombs, minimum stage size and square size.
 */
public enum GameDifficulty {
  TEST(5, 5, 5, 300, 300, 30),
  EASY(7, 7, 10, 540, 360, 30),
  MEDIUM(12, 10, 20, 680, 450, 30),
  HARD(14, 12, 40, 740, 500, 30);

  private final int gridWidth;
  private final int gridHeight;
  private final int numBombs;
  private final int stageMinWidth;
  private final int stageMinHeight;
  private final int squareSize;

  private GameDifficulty(int gridWidth, int gridHeight, int numBombs,
      int stageMinWidth, int stageMinHeight, int squareSize) {
    this.gridWidth = gridWidth;
    this.gridHeight = gridHeight;
    this.numBombs = numBombs;
    this.stageMinWidth = stageMinWidth;
    this.stageMinHeight = stageMinHeight;
    this.squareSize = squareSize;
  }

  public int getGridWidth() {
    return gridWidth;
  }

  public int getGridHeight() {
    return gridHeight;
  }

  public int getNumBombs() {
    return numBombs;
  }

  public int getStageMinWidth() {
    return stageMinWidth;
  }

  public int getStageMinHeight() {
    return stageMinHeight;
  }

  public int getSquareSize() {
    return squareSize;
  }
}
