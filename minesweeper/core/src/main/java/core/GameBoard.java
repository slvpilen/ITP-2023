package core;

import core.settings.GameDifficulty;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class represents the game board.
 * It is responsible for keeping track of the tiles and their states.
 * It also keeps track of the number of bombs left, where the bombs are placed,
 * and the number of tiles left to reveal.
 */
public class GameBoard {

  protected List<List<Tile>> board = new ArrayList<>();
  private final int width;
  private final int height;
  private final int numBombs;
  private final int[] startingCoords;
  private boolean isGameLost;
  private List<Tile> bombTiles = new ArrayList<>();
  protected int tilesLeft;
  protected int flagsLeft;
  private static Random random = new Random();

  /**
   * Creates a new game board with the specified settings.

   * @param settings The settings to use for the game board
   *     There are three possible settings: EASY, MEDIUM and HARD
   * @see GameDifficulty
   */
  public GameBoard(GameDifficulty settings) {
    this.height = settings.getGridHeight();
    this.width = settings.getGridWidth();
    this.numBombs = settings.getNumBombs();

    this.startingCoords = new int[] { -1, -1 };
    this.tilesLeft = height * width - numBombs;
    this.flagsLeft = numBombs;

    populateBoardWithTiles();
  }

  private void populateBoardWithTiles() {
    for (int y = 0; y < height; y++) {
      List<Tile> newRow = new ArrayList<>();
      for (int x = 0; x < width; x++) {
        newRow.add(new Tile(x, y));
      }

      board.add(newRow);
    }
  }

  private void setStartingCoords(int x, int y) {
    startingCoords[0] = x;
    startingCoords[1] = y;
  }

  private void placeBombs() {
    int bombsPlaced = 0;

    while (bombsPlaced < numBombs) {
      int x = random.nextInt(width);
      int y = random.nextInt(height);
      Tile tile = getTile(x, y);

      boolean adjacentToStartingX = x >= (startingCoords[0] - 1) && x <= (startingCoords[0] + 1);
      boolean adjacentToStartingY = y >= (startingCoords[1] - 1) && y <= (startingCoords[1] + 1);

      boolean validBombTile = !tile.isBomb() && !(adjacentToStartingX && adjacentToStartingY);
      if (validBombTile) {
        tile.makeBomb();
        incrementNeighborCounts(x, y);
        bombTiles.add(tile);
        bombsPlaced++;
      }
    }
  }

  /**
   * Method for testing purposes.

   * @param bombLocations The locations of the bombs you want to place,
   *     should be the same as the ones in the custom gameBoard.
   */
  protected void placeBombs(List<int[]> bombLocations) {
    if (bombLocations.size() != numBombs) {
      throw new IllegalArgumentException(
        "Number of bombs in custom gameboard does not match numBombs");
    }

    for (int[] location : bombLocations) {
      int x = location[0];
      int y = location[1];
      Tile tile = getTile(x, y);
      tile.makeBomb();
      incrementNeighborCounts(x, y);
    }
  }

  /**
   * Returns a list of all the tiles adjacent to the tile at the specified coordinate.

   * @param x the x coordinate
   * @param y the y coordinate
   * @return a list of all the tiles adjacent to the tile at the specified coordinate.
   */
  public List<Tile> getNeighborTiles(int x, int y) {
    List<Tile> neighbors = new ArrayList<>();
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        boolean validCoords = i != -1 && i != width && j != -1 && j != height;
        if (validCoords && !(i == x && j == y)) {
          neighbors.add(getTile(i, j));
        }
      }
    }
    return neighbors;
  }

  private void incrementNeighborCounts(int x, int y) {
    List<Tile> neighbors = getNeighborTiles(x, y);
    neighbors.forEach(tile -> tile.incrementNumBombsAround());
  }

  /**
   * If a tile is clicked, this method is called.
   * It does the following:
   * <pre>
   * - Checks if it is a new game 
   * - Checks if the tile is already revealed or flagged
   * - Reveals the tile and all tiles around zero-tiles 
   *   (if not revealed or flagged)
   * </pre>

   * @param x the row that was clicked
   * @param y the column that was clicked
   */
  public void tileClicked(int x, int y) {

    if (isNewGame()) {
      initializeGame(x, y);
    }

    Tile tile = getTile(x, y);

    if (tile.isBomb()) {
      tile.reveal();
      isGameLost = true;
    } else if (canRevealTile(tile)) {
      revealTileAndAdjacentIfZero(x, y);
    }
  }

  /**
   * For testing purposes.

   * @param x row
   * @param y column
   */
  void testTileClicked(int x, int y) {
    if (isNewGame()) {
      setStartingCoords(x, y);
    }

    Tile tile = getTile(x, y);

    if (tile.isBomb()) {
      tile.reveal();
      isGameLost = true;
    } else if (canRevealTile(tile)) {
      revealTileAndAdjacentIfZero(x, y);
    }
  }

  private void initializeGame(int row, int col) {
    setStartingCoords(row, col);
    placeBombs();
  }

  private void revealTileAndAdjacentIfZero(int x, int y) {
    Tile tile = getTile(x, y);
    tile.reveal();
    tilesLeft--;
    if (!tile.hasAdjacentBomb()) {
      revealAdjacent(x, y);
    }
  }

  private void revealAdjacent(int x, int y) {
    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        if (isValidCoordinateNotRevealedNotFlagged(i, j)) {
          revealTileAndAdjacentIfZero(i, j);
        }
      }
    }
  }

  /**
   * Method for testing purposes.

   * @param gameBoard The custom gameBoard
   */
  protected void setGameboard(List<List<Tile>> gameBoard) {
    this.board = gameBoard;
    List<int[]> bombLocations = findBombLocations();
    placeBombs(bombLocations);
  }

  protected List<int[]> findBombLocations() {
    List<int[]> bombLocations = new ArrayList<>();
    for (int x = 0; x < height; x++) {
      for (int y = 0; y < width; y++) {
        Tile tile = board.get(x).get(y);
        if (tile.isBomb()) {
          bombLocations.add(new int[] { x, y });
        }
      }
    }
    return bombLocations;
  }

  /**
   * Retrieves a Tile object from the game board at the specified coordinates. We are using the
   * computer-graphics convention, where the origin is in the top-left corner of the screen.
   * This means that the x-coordinate increases from left to right,
   * and the y-coordinate increases from top to bottom.

   * @param x The zero-based column index of the Tile to retrieve.
   * @param y The zero-based row index of the Tile to retrieve.
   * @return The Tile object located at the specified coordinates.
   */
  public Tile getTile(int x, int y) {
    return board.get(y).get(x);
  }

  public boolean gameIsWon() {
    return tilesLeft == 0 & !isGameLost;
  }

  public boolean isGameStarted() {
    return tilesLeft != width * height - numBombs;
  }

  public boolean canRevealTile(Tile tile) {
    return !tile.isRevealed() && !tile.isFlagged();
  }

  private boolean isValidCoordinateNotRevealedNotFlagged(int x, int y) {
    return x >= 0 && x < width && y >= 0 && y < height
        && !getTile(x, y).isRevealed() && !getTile(x, y).isFlagged();
  }

  public boolean isNewGame() {
    return startingCoords[0] == -1;
  }

  public int getFlagsLeft() {
    return flagsLeft;
  }

  public void decrementFlagsLeft() {
    flagsLeft--;
  }

  public void incrementFlagsLeft() {
    flagsLeft++;
  }

  public boolean hasFlagsLeft() {
    return getFlagsLeft() > 0;
  }

  public boolean isGameEnded() {
    return gameIsWon() || isGameLost();
  }

  public boolean isGameLost() {
    return isGameLost;
  }

  public List<Tile> getBombTiles() {
    return new ArrayList<>(bombTiles);
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int getTilesLeft() {
    return tilesLeft;
  }
}
