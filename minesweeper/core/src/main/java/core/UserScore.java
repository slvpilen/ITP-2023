package core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class is used to represent a user score, which is written to the highscore file. It contains
 * the name of the player, the score and the date.
 */
public class UserScore {

  @JsonProperty("name")
  private String name;

  @JsonProperty("score")
  private int score;

  @JsonProperty("date")
  private String date;

  @JsonProperty("difficulty")
  private String difficulty;
  
  /**
   * Constructor for the UserScore class.

   * @param name The name of the player
   * @param score The score of the player
   * @param date The date the score was achieved
   * @param difficulty The difficulty of the game
   */
  @JsonCreator
  public UserScore(@JsonProperty("name") String name, @JsonProperty("score") int score,
      @JsonProperty("date") String date, @JsonProperty("difficulty") String difficulty) {
    this.name = name;
    this.score = score;
    this.date = date;
    this.difficulty = difficulty;
  }

  /**
   * Converts the UserScore object to a JSON string JSONS strings are of the form:
   * {"name":"Roger","score":100,"date":"2021-04-20"}.

   * @return JSON string representation of the UserScore object
   */
  public String toJson() {
    ObjectMapper jsonConverter = new ObjectMapper();
    try {
      return jsonConverter.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      return "";
    }
  }

  @Override
  public String toString() {
    return "UserScore [name=" + name + ", score=" + score
      + ", date=" + date + ", difficulty=" + difficulty + "]";
  }

  public String getName() {
    return name;
  }

  public int getScore() {
    return score;
  }

  public String getDate() {
    return date;
  }

  public String getDifficulty() {
    return difficulty;
  }
}
