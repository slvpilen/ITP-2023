package core;

/**
 * Contains the state of a tile on the board.
 */
public class Tile implements TileReadable {

  private boolean isBomb;
  private boolean isFlagged;
  private boolean isRevealed;
  private int numBombsAround;

  private final int xpos;
  private final int ypos;

  /**
   * Creates a Tile at the specified location.

   * @param x the x coordinate
   * @param y the y coordinate
   */
  public Tile(int x, int y) {
    this.xpos = x;
    this.ypos = y;
  }

  @Override
  public boolean isBomb() {
    return isBomb;
  }

  @Override
  public boolean isFlagged() {
    return isFlagged;
  }

  @Override
  public boolean isRevealed() {
    return isRevealed;
  }

  @Override
  public boolean hasAdjacentBomb() {
    return this.getNumBombsAround() > 0;
  }

  public int getNumBombsAround() {
    return numBombsAround;
  }

  /**
   * Toggles the flagged status of the tile.
   */
  public void toggleFlag() {
    if (this.isRevealed()) {
      throw new IllegalStateException("Cannot flag a revealed tile");
    }
    isFlagged = !isFlagged;
  }

  /**
   * Toggles the revealed status of the tile.
   */
  public void reveal() {
    if (this.isFlagged()) {
      throw new IllegalStateException("Cannot reveal a flagged tile");
    }
    isRevealed = true;
  }

  @Override
  public int getX() {
    return xpos;
  }

  @Override
  public int getY() {
    return ypos;
  }

  protected void makeBomb() {
    isBomb = true;
  }

  protected void incrementNumBombsAround() {
    numBombsAround++;
  }

  /**
   * Returns a string representation of the path to the image 
   * that should be displayed for this tile.

   * @return the path to the image that should be displayed for this tile (string)
   */
  public String getRevealedImagePath() {
    if (isFlagged) {
      return "flag.png";
    } else if (!isRevealed) {
      return "square.jpg";
    } else if (isBomb) {
      return "bomb.png";
    } else {
      return "number" + getNumBombsAround() + ".jpg";
    }
  }

  @Override
  public String toString() {
    if (isFlagged) {
      return "F";
    }

    if (isBomb) {
      return "X";
    }

    if (!isRevealed()) {
      return "?";
    }

    if (getNumBombsAround() == 0) {
      return "";
    }

    return String.valueOf(getNumBombsAround());
  }
}
