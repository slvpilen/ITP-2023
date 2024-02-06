package core;

/**
 * This interface contains all the methods that a Tile object should have.
 */
public interface TileReadable {
  public boolean isBomb();

  public boolean isFlagged();

  public boolean isRevealed();

  public boolean hasAdjacentBomb();

  public int getNumBombsAround();

  public int getX();

  public int getY();

  public String getRevealedImagePath();
}
