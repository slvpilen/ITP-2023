package core;

import java.util.ArrayList;
import java.util.List;

import core.settings.GameDifficulty;

public class GameBoardTestUtils {

    public static GameBoard convertCharacterToGameBoard(List<List<Character>> boardWithText) {
        List<List<Tile>> convertedBoard = new ArrayList<>();
        for (List<Character> row : boardWithText) {
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
        gameBoard.setGameboard(convertedBoard);
        return gameBoard;
    }

    public static void clickOnAllEmptyTiles(GameBoard gameBoard) {
        clickOnAllEmptyTiles(gameBoard, 0);
    }

    public static void clickOnAllEmptyTiles(GameBoard gameBoard, int tilesLeft) {
        for (int x = 0; x < gameBoard.getHeight(); x++) {
            for (int y = 0; y < gameBoard.getWidth(); y++) {
                if (!gameBoard.getTile(x, y).isBomb() && gameBoard.getTilesLeft() > tilesLeft) {
                    gameBoard.testTileClicked(x, y);
                }
            }
        }

    }

    public static void printGameboard(GameBoard gameBoard) {
        for (List<Tile> row : gameBoard.board) {
            System.out.println(row);
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
