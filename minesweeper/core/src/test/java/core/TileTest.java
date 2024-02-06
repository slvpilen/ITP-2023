package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TileTest {
  

  @Test
  public void testFlagRevealedTile() {
    Tile tile = new Tile(0, 0);
    tile.reveal();
    assertThrows(IllegalStateException.class, () -> tile.toggleFlag());
  }

  @Test
  public void testRevealFlaggedTile() {
    Tile tile = new Tile(0, 0);
    tile.toggleFlag();
    assertThrows(IllegalStateException.class, () -> tile.reveal());
  }

  @Test
  public void testToString() {
    Tile tile = new Tile(0, 0);
    
    assertEquals("?", tile.toString());
    
    tile.toggleFlag();
    assertEquals("F", tile.toString());
    
    tile.toggleFlag();
    tile.reveal();
    assertEquals("", tile.toString());

    tile.incrementNumBombsAround();
    assertEquals("1", tile.toString());

    tile.makeBomb();
    assertEquals("X", tile.toString());
  }
}
