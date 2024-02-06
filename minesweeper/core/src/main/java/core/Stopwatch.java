package core;

import java.time.LocalDate;

/**
 * This class is used to measure time spent on a game.
 * The time spent on a game is measured in seconds, 
 * and is used to calculate the score of a completed
 * Minesweeper game.
 */
public class Stopwatch {
  private String date;
  protected long timeWhenStarted;
  protected int endTime;
  private boolean finished;
  protected boolean started;
  
  /**
   * Constructor for the Stopwatch class.
   * Resets the stopwatch, which sets the date to the current date,
   * and sets the time to 0.
   */
  public Stopwatch() {
    reset();
  }
  
  /**
   * Restarts the stopwatch.
   * Sets the date to the current date, and sets the time to 0.
   */
  public void reset() {
    date = "" + LocalDate.now();
    timeWhenStarted = 0;
    endTime = 0;
    finished = false;
    started = false;
  }

  /**
   * Resets the stopwatch and starts taking time.
   */
  public void start() {
    if (finished) {
      reset();
    }
    started = true;
    timeWhenStarted = System.currentTimeMillis();
  }

  /**
   * Stops the stopwatch and calculates the
   * time used since the stopwatch was started.
   */
  public void stop() {
    if (!finished && started) {
      endTime = (int) ((System.currentTimeMillis() - timeWhenStarted) / 1000);
      finished = true;
    }
  }

  /**
   * Main method for the Stopwatch class.
   * Returns the time in seconds since the stopwatch was started.

   * @return the time used in seconds since the stopwatch was started
   */
  public int getTime() {

    // Return 0 if the stopwatch has not been started.
    if (!started) {
      return 0;
    }

    // Return the already calculated time, stopwatch has not been reset yet.
    if (finished) { 
      return endTime;
    }

    int timeUsed = ((int) (System.currentTimeMillis() - timeWhenStarted) / 1000);
    boolean usedTooLongTime = timeUsed >= 999;

    if (usedTooLongTime) {
      finished = true;
      return 999;
    }

    return timeUsed; // Return the time used in seconds
  }

  public String getDate() {
    return date;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isFinished() {
    return finished;
  }
}
