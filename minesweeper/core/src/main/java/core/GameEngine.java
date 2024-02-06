package core;

import core.settings.SettingsManager;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a class which serves as a bridge between the GameBoard and the GUI.
 * It is responsible for handling user input and updating the GameBoard
 * accordingly. It also keeps track of the game's stopwatch. What essentially
 * happens is that the GUI calls the GameEngine's methods when the user
 * interacts with the GUI. The GameEngine then updates the GameBoard
 * accordingly, and returns a list of tiles which were affected by the user's
 * action. The GUI then updates the affected tiles.
 */
public class GameEngine {
  private GameBoard gameBoard;
  private Stopwatch stopwatch;
  private List<TileReadable> latestUpdatedTiles = new ArrayList<>();

  /**
   * Initializes a new game with default settings.
   */
  public GameEngine() {
    gameBoard = new GameBoard(SettingsManager.getGameDifficulty());
    stopwatch = new Stopwatch();
  }

  /**
   * Resets the game to its initial state.
   */
  public void resetGame() {
    this.gameBoard = new GameBoard(SettingsManager.getGameDifficulty());
    stopwatch.reset();
    latestUpdatedTiles.clear();
  }

  /**
   * Handles a left click action at the specified coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void handleLeftClick(int x, int y) {
    latestUpdatedTiles.clear();

    TileReadable clickedTile = getTile(x, y);
    if (clickedTile.isFlagged()) {
      return;
    }

    gameBoard.tileClicked(x, y);

    if (clickedTile.isBomb()) {
      handleBombClicked();
      return;
    }

    addRevealedTilesToLatestUpdated();

    if (!stopWatchIsStarted()) {
      stopwatch.start();
    }

    if (isGameEnded()) {
      stopwatch.stop();
    }
  }

  /**
   * Handles a right click action at the specified coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void handleRightClick(int x, int y) {
    latestUpdatedTiles.clear();
    if (!gameBoard.isGameStarted()) {
      return;
    }

    Tile clickedTile = gameBoard.getTile(x, y);
    if (canToggleFlag(clickedTile)) {
      toggleTileFlag(clickedTile);
    }
  }

  /**
   * Handles a space bar click action at the specified coordinates.
   * 
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void handleSpaceBarClick(int x, int y) {
    Tile clickedTile = gameBoard.getTile(x, y);
    if (!clickedTile.isRevealed()) {
      return;
    }

    List<Tile> neighbors = gameBoard.getNeighborTiles(x, y);
    boolean correctNumberOfFlags = clickedTile.getNumBombsAround() == neighbors.stream().filter(Tile::isFlagged)
        .count();

    if (!correctNumberOfFlags) {
      return;
    }

    neighbors.stream().filter(tile -> !tile.isFlagged() && !tile.isBomb())
        .forEach(tile -> handleLeftClick(tile.getX(), tile.getY()));
    neighbors.stream().filter(tile -> !tile.isFlagged() && tile.isBomb())
        .forEach(tile -> handleLeftClick(tile.getX(), tile.getY()));

    addRevealedTilesToLatestUpdated();
  }

  /**
   * Handles the event when a bomb is clicked. It stops the game's stopwatch and
   * reveals all bomb tiles.
   */
  private void handleBombClicked() {
    List<Tile> bombTiles = gameBoard.getBombTiles();
    bombTiles.forEach(tile -> {
      if (tile.isFlagged()) {
        tile.toggleFlag();
      }
      tile.reveal();
    });
    latestUpdatedTiles.addAll(bombTiles);

    stopwatch.stop();
  }

  private void toggleTileFlag(Tile tile) {
    tile.toggleFlag();

    if (tile.isFlagged()) {
      gameBoard.decrementFlagsLeft();
    } else {
      gameBoard.incrementFlagsLeft();
    }
    latestUpdatedTiles.add(tile);
  }

  private boolean canToggleFlag(Tile tile) {
    return !tile.isRevealed() && gameBoard.hasFlagsLeft() || tile.isFlagged();
  }

  private void addRevealedTilesToLatestUpdated() {
    for (int i = 0; i < gameBoard.getWidth(); i++) {
      for (int j = 0; j < gameBoard.getHeight(); j++) {
        TileReadable tile = getTile(i, j);
        if (tile.isRevealed()) {
          latestUpdatedTiles.add(tile);
        }
      }
    }
  }

  public boolean stopWatchIsStarted() {
    return stopwatch.isStarted();
  }

  public List<TileReadable> getLatestUpdatedTiles() {
    return new ArrayList<>(latestUpdatedTiles);
  }

  public int getFlagsLeft() {
    return gameBoard.getFlagsLeft();
  }

  public boolean isGameWon() {
    return gameBoard.gameIsWon();
  }

  public boolean isGameLost() {
    return gameBoard.isGameLost();
  }

  public TileReadable getTile(int x, int y) {
    return gameBoard.getTile(x, y);
  }

  public boolean isGameStarted() {
    return gameBoard.isGameStarted();
  }

  public int getTime() {
    return stopwatch.getTime();
  }

  public boolean isGameEnded() {
    return gameBoard.isGameEnded();
  }

  public String getDate() {
    return stopwatch.getDate();
  }

  public Stopwatch getStopwatch() {
    return stopwatch;
  }

  // For testing
  public List<Tile> getNeighborTiles(int x, int y) {
    return gameBoard.getNeighborTiles(x, y);
  }

}
