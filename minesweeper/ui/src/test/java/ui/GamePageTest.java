package ui;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import core.GameEngine;
import core.Tile;
import core.TileReadable;
import core.UserScore;
import core.savehandler.HighscoreFileManager;
import core.settings.GameDifficulty;
import core.settings.SettingsManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class GamePageTest extends ApplicationTest {

  private GamePageController gamePageController;
  private Parent root;
  private GridPane gameGrid;
  private FxRobot robot;

  @Override
  public void start(Stage stage) throws IOException {

    // Setting up a mock of HTTP requests.
    RestRequest mockz = Mockito.mock(RestRequest.class);
    Mockito.doAnswer(inv -> {
      UserScore score = inv.getArgument(0, UserScore.class);
      HighscoreFileManager.writeToHighscore(score, HighscoreFileManager.getFile());
      return null;
    }).when(mockz).writeToHighscore(Mockito.any(UserScore.class));

    GamePageController ctrl = new GamePageController();
    ctrl.setRestRequest(mockz);
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/GamePage.fxml"));
    fxmlLoader.setControllerFactory(cls -> ctrl);
    root = fxmlLoader.load();
    gamePageController = fxmlLoader.getController();

    stage.setScene(new Scene(root));
    MineApp.setStageSize(stage, SettingsManager.getStageMinWidth(), SettingsManager.getStageMinHeight());
    stage.show();
    robot = new FxRobot();
    gameGrid = robot.lookup("#gameGrid").query();
    SettingsManager.setGameDifficulty(GameDifficulty.EASY);
  }

  /**
   * Clicks on the labels in the fxml-file which has the same name as the string
   * parameters.
   * 
   * @param labels A list of strings which are the names of the labels you want to
   *               click on.
   */
  private void click(String... labels) {
    for (String label : labels) {
      clickOn(LabeledMatchers.hasText(label));
    }
  }

  @Test
  public void testResetGame() {
    clickOn((Node) gameGrid.getChildren().get(0));
    assertTrue(gamePageController.getStarted());
    click("Reset game");
    assertFalse(gamePageController.getStarted());
  }

  /**
   * A test where we click on all the tiles that are not bombs. The game should be
   * won after this.
   */
  @Test
  public void testWin() {
    assertInitialState();

    GameEngine engine = gamePageController.getGameEngine();
    clickOn(gameGrid.getChildren().get(0));

    for (Node node : gameGrid.getChildren()) {
      // Coordinate of the node we click on / (Tile)
      int x = GridPane.getColumnIndex(node);
      int y = GridPane.getRowIndex(node);

      TileReadable tile = engine.getTile(x, y);
      if (tile.isRevealed()) {
        continue;
      }

      if (tile.isBomb()) {
        rightClickOn(node);

      } else {
        clickOn(node);
      }
    }
    assertGameWon();
  }

  private void assertInitialState() {
    assertEquals(false, robot.lookup("#leaderBoardNameLabel").queryLabeled().isVisible());
    assertEquals(false, robot.lookup("#nameField").query().isVisible());
    assertEquals(false, robot.lookup("#sendToLeaderBoardButton").query().isVisible());
    assertEquals(false, !robot.lookup("#nameField").query().isDisabled());
    assertEquals(false, !robot.lookup("#sendToLeaderBoardButton").query().isDisabled());
  }

  private void assertGameWon() {
    assertEquals(true, robot.lookup("#leaderBoardNameLabel").queryLabeled().isVisible());
    assertEquals(true, robot.lookup("#nameField").query().isVisible());
    assertEquals(true, robot.lookup("#sendToLeaderBoardButton").query().isVisible());
    assertEquals(true, !robot.lookup("#nameField").query().isDisabled());
    assertEquals(true, !robot.lookup("#sendToLeaderBoardButton").query().isDisabled());

    robot.lookup("#nameField").queryTextInputControl().setText("TesterAAAAA");
    click("OK");

    assertEquals(true, HighscoreFileManager.readFromHighscore(new File("./../appdata/highscore.json")).stream()
        .anyMatch(score -> score.getName().equals("TesterAAAAA")));
    HighscoreFileManager.deleteFromHighscore("TesterAAAAA", gamePageController.getTime(), gamePageController.getDate(),
        SettingsManager.getGameDifficultyAsString(), HighscoreFileManager.getFile());
  }

  @Test
  @DisplayName("Ensure clicking on a bomb make you lose")
  public void testLose() {
    clickOn((Node) gameGrid.getChildren().get(0));
    clickOn(findFittingNode(t -> t.isBomb()));
    assertEquals("Game over!", robot.lookup("#gameStatusLabel").queryLabeled().getText(), "Game should be over");
  }

  @Test
  @DisplayName("Ensure that the choording works")
  public void testChoording() {
    TileReadable tileToClick = findSuitableSpacebarTile(2);
    List<Tile> neighborTiles = flagAllNeighborBombs(tileToClick.getX(), tileToClick.getY());

    Node tileToClickNode = getNodeFromGridPane(tileToClick.getX(), tileToClick.getY());
    clickOn(tileToClickNode);
    push(KeyCode.SPACE);

    for (Tile tile : neighborTiles) {
      assertEquals(true, tile.isRevealed() || tile.isFlagged(), "Tiles should be flagged or revealed after choording");
    }
  }

  @Test
  public void testSwitchToSettings() {
    assertEquals(false, robot.lookup("#easyButton").tryQuery().isPresent());
    click("Settings");
    assertEquals(true, robot.lookup("#easyButton").tryQuery().isPresent());
  }

  /**
   * This method tests if the leaderboard is shown when the leaderboard button is
   * clicked. The standard output is redirected to a ByteArrayOutputStream, such
   * that we don't get the error messages caused by the fact that the HTTP-
   * requests are not working, since we are not running the server.
   */
  @Test
  public void testSwitchToLeaderboard() {
    PrintStream originalOut = System.out;
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outContent));
    assertEquals(false, robot.lookup("#score1").tryQuery().isPresent());
    click("Leaderboard");
    assertEquals(true, robot.lookup("#score1").tryQuery().isPresent());
    System.setOut(originalOut);
  }

  /**
   * Just checking that nothing happens when you press buttons which are
   * irrelevant to the game.
   */
  @Test
  public void testThatOtherButtonsDoNotAffectGame() {
    TileReadable tileToClick = findSuitableSpacebarTile(2);
    Node tileToClickNode = getNodeFromGridPane(tileToClick.getX(), tileToClick.getY());

    List<Tile> neighborTiles = flagAllNeighborBombs(tileToClick.getX(), tileToClick.getY());
    List<Tile> revealedNeighbors = neighborTiles.stream().filter(Tile::isRevealed).toList();

    clickOn(tileToClickNode);
    push(KeyCode.U);

    assertFalse(neighborTiles.stream().anyMatch(t -> t.isRevealed() && !revealedNeighbors.contains(t)),
        "Tiles should not be revealed when clicking U instead of spacebar");

    click("Reset game");
    // Mousewheel click
    clickOn((Node) gameGrid.getChildren().get(0), MouseButton.MIDDLE);
    assertFalse(gamePageController.getStarted());
  }

  @Test
  public void clickFlaggedTile() {
    clickOn((Node) gameGrid.getChildren().get(0), MouseButton.SECONDARY);
    assertEquals(false, gamePageController.getTile(0, 0).isFlagged(),
        "You should not be able to flag a tile before the game has started");

    clickOn((Node) gameGrid.getChildren().get(0));
    assertTrue(gamePageController.getTile(0, 0).isRevealed());
    clickOn((Node) gameGrid.getChildren().get(0), MouseButton.SECONDARY);
    assertFalse(gamePageController.getTile(0, 0).isFlagged(),
        "You should not be able to flag a tile after it has been revealed");

    TileReadable tileToClick = findFirstRevealedTileWithBombNearby();
    flagAllNeighborBombs(tileToClick.getX(), tileToClick.getY());
    Node flaggedNode = findFirstFlaggedNeighbor(tileToClick.getX(), tileToClick.getY());
    TileReadable flaggedTile = gamePageController.getTile(GridPane.getColumnIndex(flaggedNode),
        GridPane.getRowIndex(flaggedNode));
    clickOn(flaggedNode);
    assertFalse(flaggedTile.isRevealed(), "You should not be able to reveal a flagged tile");
  }

  @Test
  public void spaceBarBeforeGameStart() {
    push(KeyCode.SPACE);
    assertFalse(gamePageController.getStarted(), "You should not be able to start the game by pressing spacebar");
  }

  /**
   * We don't want anything to happen when we click spacebar, if we are not
   * hovering over a tile.
   */
  @Test
  public void spaceBarDoesNotReset() {
    clickOn((Node) gameGrid.getChildren().get(0));
    clickOn("Reset game", MouseButton.SECONDARY); // We want the mouse to be in the right place.
    push(KeyCode.SPACE); // Click spacebar while hovering over the reset button.
    assertTrue(gamePageController.getTile(0, 0).isRevealed(), "Spacebar should not be able to reset the game");
  }

  /**
   * This tests that if you have wrongly flagged a non-bomb and a bomb, and then
   * you press spacebar, the game should end in a loss.
   * 
   * @throws Exception If thread.sleep fails.
   */
  @Test
  public void spaceBarFlaggedWrong() throws Exception {
    TileReadable tileToClick = findSuitableSpacebarTile(2);
    Node nodeToClick = getNodeFromGridPane(tileToClick.getX(), tileToClick.getY());

    List<Tile> neighborTiles = flagAllNeighborBombs(tileToClick.getX(), tileToClick.getY());
    clickOn(nodeToClick, MouseButton.SECONDARY);
    clickOn(nodeToClick, MouseButton.SECONDARY); // Just to get the mouse to the right place
    push(KeyCode.SPACE); // Doing the spacebar move without having revealed the middle tile.

    assertFalse(neighborTiles.stream().anyMatch(t -> t.isRevealed() && !t.isFlagged()),
        "Tiles should not be revealed since middle tile is not revealed");

    clickOn(nodeToClick); // Revealing the middle tile.
    flagAllNeighborBombs(tileToClick.getX(), tileToClick.getY()); // Unflagging the bombs.

    List<Tile> neighborBombs = neighborTiles.stream().filter(t -> t.isBomb()).toList();
    List<Tile> unrevealedNonBombs = neighborTiles.stream().filter(t -> !t.isRevealed() && !t.isBomb()).toList();

    // Flagging one bomb and one non-bomb.
    clickOn(getNodeFromGridPane(neighborBombs.get(0).getX(), neighborBombs.get(0).getY()), MouseButton.SECONDARY);
    clickOn(getNodeFromGridPane(unrevealedNonBombs.get(0).getX(), unrevealedNonBombs.get(0).getY()),
        MouseButton.SECONDARY);

    clickOn(nodeToClick); // Just to get the mouse to the right place
    push(KeyCode.SPACE); // This move should result in a loss, since we have flagged one bomb and one
                         // non-bomb.

    Thread.sleep(200);
    for (Tile tile : neighborTiles) {
      assertEquals(true, tile.isRevealed() || tile.isFlagged(),
          "Tiles should be flagged or revealed after a spacebar move");
    }

    assertTrue(gamePageController.getGameEngine().isGameLost(), "Game should be lost");
  }

  @Test
  public void noActionsAfterGameEnded() {

    TileReadable tileToClick = findSuitableSpacebarTile(2);
    Node nodeToClick = getNodeFromGridPane(tileToClick.getX(), tileToClick.getY());
    List<Tile> neighborTiles = flagAllNeighborBombs(tileToClick.getX(), tileToClick.getY());

    Node elBombo = findFittingNode(t -> t.isBomb() && !t.isFlagged());
    clickOn(elBombo); // Clicking on a bomb and losing.

    // Pressing spacebar after the game has ended should not do anything.
    clickOn(nodeToClick);
    push(KeyCode.SPACE);
    assertTrue(neighborTiles.stream().anyMatch(t -> !t.isRevealed() && !t.isFlagged()),
        "Spacebar should not reveal any tiles after the game has ended");

    // Game should be over now, and clicking on a tile should not do anything.
    Node fitNode = findFittingNode(t -> !t.isBomb() && !t.isRevealed());
    clickOn(fitNode);
    TileReadable clickedTile = gamePageController.getTile(GridPane.getColumnIndex(fitNode),
        GridPane.getRowIndex(fitNode));
    assertFalse(clickedTile.isRevealed(), "You should not be able to click on a tile after the game has ended");

    // Check that you cannot flag a tile after the game has ended.
    fitNode = findFittingNode(t -> !t.isFlagged() && !t.isRevealed());
    clickOn(fitNode, MouseButton.SECONDARY);
    clickedTile = gamePageController.getTile(GridPane.getColumnIndex(nodeToClick), GridPane.getRowIndex(nodeToClick));
    assertFalse(clickedTile.isFlagged(), "You should not be able to flag a tile after the game has ended");
  }

  @Test
  public void testLimitedNumberOfFlags() {
    clickOn((Node) gameGrid.getChildren().get(6));

    make10TilesFlagged();

    Pair<TileReadable, Node> notFlaggedTileAndNode = getNotFlaggedTileAndNode();

    clickOn(notFlaggedTileAndNode.getValue(), MouseButton.SECONDARY);
    assertFalse(notFlaggedTileAndNode.getKey().isFlagged(),
        "You should not be able to flag more than 10 tiles when playing on EASY mode.");

    // We should be able to unflag a tile.
    Node noldus = findFittingNode(t -> t.isFlagged());
    TileReadable tile = gamePageController.getTile(GridPane.getColumnIndex(noldus), GridPane.getRowIndex(noldus));
    clickOn(noldus, MouseButton.SECONDARY);
    assertFalse(tile.isFlagged(), "You should be able to unflag a tile.");
  }

  private void make10TilesFlagged() {
    int counter = 0;
    for (Node node : gameGrid.getChildren()) {
      int x = GridPane.getColumnIndex(node);
      int y = GridPane.getRowIndex(node);

      TileReadable tile = gamePageController.getTile(x, y);

      if (!tile.isRevealed() && !tile.isFlagged()) {
        clickOn(node, MouseButton.SECONDARY);
        counter++;
      }
      if (counter == 10) {
        break;
      }
    }
  }

  private Pair<TileReadable, Node> getNotFlaggedTileAndNode() {
    for (Node node : gameGrid.getChildren()) {
      int x = GridPane.getColumnIndex(node);
      int y = GridPane.getRowIndex(node);

      TileReadable tile = gamePageController.getTile(x, y);

      if (!tile.isFlagged() && !tile.isRevealed()) {
        return new Pair<>(tile, node);
      }
    }
    throw new IllegalStateException("There should be at least one tile which is not flagged.");
  }

  @Test
  public void flagsRemovedAfterBombClick() {

    clickOn((Node) gameGrid.getChildren().get(48));
    List<Node> bombs = getBombNodes();
    List<Node> regularSquares = getUnrevealedNonBombNodes();

    // Place flags on 3 bombs and 3 regular squares.
    for (int i = 0; i < 3; i++) {
      clickOn(regularSquares.get(i), MouseButton.SECONDARY);
    }
    for (int i = 0; i < 3; i++) {
      clickOn(bombs.get(i), MouseButton.SECONDARY);
    }

    // Check that the flags are placed.
    for (int i = 0; i < 3; i++) {
      assertTrue(gamePageController
          .getTile(GridPane.getColumnIndex(regularSquares.get(i)), GridPane.getRowIndex(regularSquares.get(i)))
          .isFlagged(), "Tiles should be flagged before losing.");
    }

    for (int i = 0; i < 3; i++) {
      assertTrue(gamePageController.getTile(GridPane.getColumnIndex(bombs.get(i)), GridPane.getRowIndex(bombs.get(i)))
          .isFlagged(), "Tiles should be flagged before losing.");
    }

    clickOn(bombs.get(3)); // Click on a bomb and lose.

    // Check that the flags on the regular squares are still there.
    for (int i = 0; i < 3; i++) {
      assertTrue(gamePageController
          .getTile(GridPane.getColumnIndex(regularSquares.get(i)), GridPane.getRowIndex(regularSquares.get(i)))
          .isFlagged(), "Flags should remain on non-bomb tiles.");
    }

    for (int i = 0; i < 3; i++) {
      assertFalse(gamePageController.getTile(GridPane.getColumnIndex(bombs.get(i)), GridPane.getRowIndex(bombs.get(i)))
          .isFlagged(), "Flags should be removed from bomb tiles.");
    }
  }

  @Test
  public void doubleRightClick() {
    clickOn((Node) gameGrid.getChildren().get(42));
    Node nody = gameGrid.getChildren().stream()
        .filter(
            node -> !gamePageController.getTile(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)).isRevealed())
        .findFirst().get();
    TileReadable tile = gamePageController.getTile(GridPane.getColumnIndex(nody), GridPane.getRowIndex(nody));
    clickOn(nody, MouseButton.SECONDARY);
    assertTrue(tile.isFlagged(), "Tile should be flagged after right clicking");
    clickOn(nody, MouseButton.SECONDARY);
    assertFalse(tile.isFlagged(), "Flagged tile should no longer be flagged after right clicking again");
  }

  @Test
  public void spaceBarIncorrectNumberOfFlags() {
    TileReadable tileToClick = findSuitableSpacebarTile(2);
    List<Tile> neighborTiles = getNeighborTiles(tileToClick.getX(), tileToClick.getY());
    List<Node> neighborBombs = neighborTiles.stream().filter(t -> t.isBomb())
        .map(t -> getNodeFromGridPane(t.getX(), t.getY())).toList();
    clickOn(neighborBombs.get(0), MouseButton.SECONDARY);

    Node tileToClickNode = getNodeFromGridPane(tileToClick.getX(), tileToClick.getY());
    clickOn(tileToClickNode); // Just to get the mouse to the right place.
    push(KeyCode.SPACE);

    assertTrue(neighborTiles.stream().anyMatch(t -> !t.isRevealed() && !t.isFlagged()),
        "Tiles should not be revealed if the number of flags is incorrect");

  }

  @Test
  public void squareWithNoSurroundingBombsFlagged() {

    Node flaggedNode = null;
    Node nodeToClick = null;
    TileReadable flaggedTile = null;
    TileReadable clickedTile = null;
    TileReadable autoReveal = null;

    boolean foundSuitableTile = false;

    while (!foundSuitableTile) {
      click("Reset game");
      clickOn((Node) gameGrid.getChildren().get(0));

      for (Node node : gameGrid.getChildren()) {
        int x = GridPane.getColumnIndex(node);
        int y = GridPane.getRowIndex(node);

        TileReadable tile = gamePageController.getTile(x, y);
        if (!tile.isRevealed() && !tile.isBomb() && !tile.hasAdjacentBomb()) {

          List<Tile> neighbors = getNeighborTiles(x, y);
          long numberOfZeros = neighbors.stream().filter(t -> !t.isRevealed() && !t.hasAdjacentBomb()).count();

          if (numberOfZeros > 1) {
            nodeToClick = node;
            clickedTile = tile;
            List<Tile> tiles = neighbors.stream().filter(t -> !t.isRevealed() && !t.isBomb() && !t.hasAdjacentBomb())
                .toList();
            flaggedTile = tiles.get(0);
            flaggedNode = getNodeFromGridPane(flaggedTile.getX(), flaggedTile.getY());
            autoReveal = tiles.get(1);
            foundSuitableTile = true;
          }
        }
      }
    }

    assertFalse(clickedTile.isRevealed() || flaggedTile.isRevealed() || autoReveal.isRevealed(),
        "None of the tiles should be revealed before clicking");
    clickOn(flaggedNode, MouseButton.SECONDARY);
    clickOn(nodeToClick);

    assertTrue(clickedTile.isRevealed(), "Tile should be revealed");
    assertTrue(flaggedTile.isFlagged(), "Tile should be flagged");
    assertTrue(!flaggedTile.isRevealed(), "Flagged tile should not be revealed, even if it is an auto-reveal tile");
    assertTrue(autoReveal.isRevealed(), "Auto-reveal tile should be revealed");
  }

  // For testing
  public Node getNodeFromGridPane(int col, int row) {
    return gameGrid.getChildren().stream()
        .filter(node -> GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row).findFirst()
        .orElse(null);
  }

  public Node findFittingNode(Predicate<TileReadable> pred) {
    return gameGrid.getChildren().stream()
        .filter(
            node -> pred.test(gamePageController.getTile(GridPane.getColumnIndex(node), GridPane.getRowIndex(node))))
        .findFirst().orElse(null);
  }

  /**
   * Finds the first revealed tile with a bomb nearby.
   * 
   * @return The first revealed tile with a bomb nearby. If no such tile exists,
   *         null is returned.
   */
  private TileReadable findFirstRevealedTileWithBombNearby() {
    TileReadable tileToClick = null;
    for (Node node : gameGrid.getChildren()) {
      int rowIndex = GridPane.getRowIndex(node);
      int columnIndex = GridPane.getColumnIndex(node);

      TileReadable tile = gamePageController.getTile(columnIndex, rowIndex);
      if (!tile.isBomb() && tile.hasAdjacentBomb() && tile.isRevealed()) {
        tileToClick = tile;
        return tileToClick;
      }
    }
    return null;
  }

  /**
   * We want to find an unrevealed tile which has number of bombs nearby equal to
   * param numberOfBombs. We also want this tile to have only unrevealed
   * neighbors. The method starts by resetting the game.
   * 
   * @param numberOfBombs The number of bombs we want the tile to have nearby.
   * @return A tile which is revealed, has at least one bomb nearby, and has at
   *         least one unrevealed neighbor which is not a bomb.
   */
  private TileReadable findSuitableSpacebarTile(int numberOfBombs) {

    TileReadable tileToClick = null;

    for (;;) {

      click("Reset game");
      clickOn((Node) gameGrid.getChildren().get(0));

      for (Node node : gameGrid.getChildren()) {
        int rowIndex = GridPane.getRowIndex(node);
        int columnIndex = GridPane.getColumnIndex(node);

        TileReadable tile = gamePageController.getTile(columnIndex, rowIndex);
        if (tile.isRevealed() || tile.isBomb()) {
          continue;
        }

        List<Node> neighborNodes = getNeighborNodes(columnIndex, rowIndex);
        List<Node> unrevealedNonBombNeighbors = neighborNodes.stream()
            .filter(n -> !gamePageController.getTile(GridPane.getColumnIndex(n), GridPane.getRowIndex(n)).isRevealed()
                && !gamePageController.getTile(GridPane.getColumnIndex(n), GridPane.getRowIndex(n)).isBomb())
            .toList();

        List<Tile> neighborTiles = getNeighborTiles(columnIndex, rowIndex);
        // We need all neighbors to be unrevealed.
        if (unrevealedNonBombNeighbors.size() + numberOfBombs != neighborTiles.size()) {
          continue;
        }
        long neighborBombs = neighborTiles.stream().filter(t -> t.isBomb()).count();

        if (neighborBombs == numberOfBombs && unrevealedNonBombNeighbors.size() > 0) {
          tileToClick = tile;
          return tileToClick;
        }
      }
    }
  }

  /**
   * Flags all the bombs around a tile.
   * 
   * @param x The row of the tile.
   * @param y The column of the tile.
   * @return A list of all the neighbor tiles.
   */
  private List<Tile> flagAllNeighborBombs(int x, int y) {
    List<Tile> neighborTiles = gamePageController.getNeighborTiles(x, y);
    for (TileReadable tile : neighborTiles) {
      if (tile.isBomb()) {
        rightClickOn(getNodeFromGridPane(tile.getX(), tile.getY()));
      }
    }
    return neighborTiles;
  }

  /**
   * Returns a flagged neighbor Node.
   * 
   * @param x The row of the tile.
   * @param y The column of the tile.
   * @return A node which corresponds to a flagged neighbor tile.
   */
  private Node findFirstFlaggedNeighbor(int x, int y) {
    List<Tile> neighborTiles = gamePageController.getNeighborTiles(x, y);
    for (TileReadable tile : neighborTiles) {
      if (tile.isFlagged()) {
        return getNodeFromGridPane(tile.getX(), tile.getY());
      }
    }
    return null;
  }

  /**
   * Returns a list of all the bomb nodes in the game grid.
   * 
   * @return A list of all the bomb nodes in the game grid.
   */
  private List<Node> getBombNodes() {
    return gameGrid.getChildren().stream()
        .filter(node -> gamePageController.getTile(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)).isBomb())
        .toList();
  }

  /**
   * Returns a list of all the unrevealed non-bomb nodes in the game grid.
   * 
   * @return A list of all the unrevealed non-bomb nodes in the game grid.
   */
  private List<Node> getUnrevealedNonBombNodes() {
    return gameGrid.getChildren().stream()
        .filter(node -> !gamePageController.getTile(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)).isBomb()
            && !gamePageController.getTile(GridPane.getColumnIndex(node), GridPane.getRowIndex(node)).isRevealed())
        .toList();
  }

  /**
   * Returns a list of all the neighbor nodes of the tile at the specified
   * coordinate.
   * 
   * @param x Row number
   * @param y Column number
   * @return A list of all the neighbor nodes of the tile at the specified
   *         coordinate.
   */
  private List<Node> getNeighborNodes(int x, int y) {
    return gamePageController.getNeighborTiles(x, y).stream().map(tile -> getNodeFromGridPane(tile.getX(), tile.getY()))
        .toList();
  }

  /**
   * Returns a list of all the tiles adjacent to the tile at the specified
   * coordinate.
   * 
   * @param x Row number
   * @param y Column number
   * @return A list of all the tiles adjacent to the tile at the specified
   *         coordinate.
   */
  private List<Tile> getNeighborTiles(int x, int y) {
    return gamePageController.getNeighborTiles(x, y);
  }

}
