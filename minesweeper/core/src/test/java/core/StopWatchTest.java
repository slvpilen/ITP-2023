package core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StopWatchTest {

  private Stopwatch stopWatch;

  @BeforeEach
  public void setUp() {
    stopWatch = new Stopwatch();
  }

  @Test
  public void testReset() {
    stopWatch.start();
    stopWatch.reset();
    assertEquals(0, stopWatch.getTime());
    assertFalse(stopWatch.isStarted());
  }

  /**
   * Tests all the different combinations of started/stopped
   * you can have when stopping a stopwatch.
   */
  @Test
  public void stopScenarios() {

    // Not started nor stopped
    stopWatch.stop();
    assertFalse(stopWatch.isStarted());
    assertFalse(stopWatch.isFinished());
    assertEquals(0, stopWatch.getTime());

    // Started but not stopped
    stopWatch.start();
    stopWatch.stop();
    assertEquals(0, stopWatch.getTime());

    stopWatch.start();
    stopWatch.timeWhenStarted = (System.currentTimeMillis() - 1200);
    stopWatch.stop();
    assertEquals(1, stopWatch.getTime(), "Stopwatch should be able to stop after being started again");

    // Started and stopped
    stopWatch.timeWhenStarted = (System.currentTimeMillis() - 1200);
    stopWatch.stop();
    assertEquals(1, stopWatch.getTime(), "Stopwatch has already been stopped, should not be able to stop again");

    // It is not possible to have a stopwatch be stopped but not started.
    // We'll test the situation anyway.
    stopWatch.reset();
    stopWatch.start();
    stopWatch.timeWhenStarted = (System.currentTimeMillis() - 1200);
    stopWatch.stop();
    stopWatch.started = false;
    assertEquals(0, stopWatch.getTime(), "Stopwatch should not be able to be stopped if it is not started");
  }

  @Test
  public void usedTooLongTime() {
    stopWatch.start();
    // Use 50 minutes, or 3000 seconds
    stopWatch.timeWhenStarted = (System.currentTimeMillis() - 3_000_000);
    assertEquals(999, stopWatch.getTime());

    stopWatch.reset();
    stopWatch.timeWhenStarted = (System.currentTimeMillis() - 3_000_000);
    assertEquals(0, stopWatch.getTime(), "Stopwatch should not be able to be stopped if it is not started");
  }
}
