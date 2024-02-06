package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.settings.GameDifficulty;

public class GameBoardTest {

  @Test
  @DisplayName("Ensure that the constructor works for all difficulties")
  public void testGameBoard() {
    GameBoard gameBoard = new GameBoard(GameDifficulty.EASY);
    Assertions.assertEquals(false, gameBoard.gameIsWon(),
        "A new gameBoard should not be considered as won.");

    gameBoard = new GameBoard(GameDifficulty.MEDIUM);
    Assertions.assertEquals(false, gameBoard.gameIsWon(),
        "A new gameBoard should not be considered as won.");

    gameBoard = new GameBoard(GameDifficulty.HARD);
    Assertions.assertEquals(false, gameBoard.gameIsWon(),
        "A new gameBoard should not be considered as won.");

  }

  @Test
  @DisplayName("Ensure that clicking on first tiles and tiles around it works correctly")
  public void testClickOnTile() {
    GameBoard gameBoard = new GameBoard(GameDifficulty.MEDIUM);
    gameBoard.tileClicked(1, 1);

    List<int[]> coordinatesWithNoBombs = Arrays.asList(new int[] { 0, 0 }, new int[] { 0, 1 },
        new int[] { 0, 2 }, new int[] { 1, 0 }, new int[] { 1, 2 }, new int[] { 2, 0 },
        new int[] { 2, 1 },
        new int[] { 2, 2 });

    Tile clickedTile = gameBoard.getTile(1, 1);
    assertEquals(true, clickedTile.isRevealed(),
        "First click should always be reveald.");

    for (int[] coordinates : coordinatesWithNoBombs) {
      Tile tile = gameBoard.getTile(coordinates[0], coordinates[1]);
      assertEquals(false, tile.isBomb(),
          "All tiles around first clickes should not be bombs.");
      assertEquals(true, tile.isRevealed(),
          "All tiles around first clickes should be revealed.");
      gameBoard.tileClicked(coordinates[0], coordinates[1]);
    }

    assertEquals(false, gameBoard.gameIsWon(),
        "Game should not be won when all tiles around first click is clicked.");
  }

  @Test
  @DisplayName("Ensure game winning works correctly")
  public void testWinningGame() {

    // 'E' equals empty and 'B' equals bomb
    List<List<Character>> board5x5 = Arrays.asList(
        Arrays.asList('E', 'E', 'E', 'E', 'B'),
        Arrays.asList('E', 'B', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'E', 'E', 'E'));
    GameBoard gameBoard = GameBoardTestUtils.convertCharacterToGameBoard(board5x5);

    gameBoard.testTileClicked(0, 0);
    GameBoardTestUtils.clickOnAllEmptyTiles(gameBoard);

    assertEquals(0, gameBoard.tilesLeft,
        "Every Tile except bombs should be clicked");
    assertEquals(true, gameBoard.gameIsWon(),
        "When all not bomb tiles is clicked game should be won");
  }

  @Test
  @DisplayName("Test that the game is lost when a bomb is clicked")
  public void testLosingGame() {
    List<List<Character>> board5x5 = Arrays.asList(
        Arrays.asList('E', 'E', 'E', 'E', 'B'),
        Arrays.asList('E', 'B', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'E', 'E', 'E'));
    GameBoard gameBoard = GameBoardTestUtils.convertCharacterToGameBoard(board5x5);

    gameBoard.testTileClicked(0, 0);
    gameBoard.testTileClicked(0, 4);
    GameBoardTestUtils.clickOnAllEmptyTiles(gameBoard, 1);

    assertEquals(1, gameBoard.tilesLeft,
        "All but one tile should be clicked");
    assertEquals(true, gameBoard.getTile(0, 4).isRevealed(),
        "The bomb should be revealed");
    assertEquals(false, gameBoard.gameIsWon(),
        "When a bomb is clicked the game should be lost");

    GameBoardTestUtils.clickOnAllEmptyTiles(gameBoard);

    assertEquals(0, gameBoard.tilesLeft,
        "All but one tile should be clicked");
    assertEquals(true, gameBoard.getTile(0, 4).isRevealed(),
        "The bomb should be revealed");
    assertEquals(false, gameBoard.gameIsWon(),
        "When a bomb is clicked the game should be lost");

  }

  /**
   * Setting up a board with only 4 bombs, but the TEST
   * difficulty requires 5 bombs.
   */
  @Test
  public void testWrongNumberOfBombs() {
    List<List<Character>> board5x5 = Arrays.asList(
        Arrays.asList('E', 'E', 'E', 'E', 'E'),
        Arrays.asList('E', 'B', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'B', 'E', 'E'),
        Arrays.asList('E', 'E', 'E', 'E', 'E'));

    List<List<Tile>> convertedBoard = new ArrayList<>();
    for (List<Character> row : board5x5) {
      List<Tile> newRow = new ArrayList<>();
      for (char tileChar : row) {
        Tile newTile = new Tile(0, 0);
        if (tileChar == 'B')
          newTile.makeBomb();
        newRow.add(newTile);
      }
      convertedBoard.add(newRow);
    }

    GameBoard gameBoard = new GameBoard(GameDifficulty.TEST);
    assertThrows(IllegalArgumentException.class, () -> {
      gameBoard.setGameboard(convertedBoard);
    });
  }

}
