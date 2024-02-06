package core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;


public class UserScoreTest {

  private static class ExceptionUserScore extends UserScore {
    public ExceptionUserScore(String name, int score, String date, String difficulty) {
      super(name, score, date, difficulty);
    }

    // We are adding a property ExceptionalField : value,
    // which will throw a JsonProcessingException when we try to serialize it.
    // This way we can test that the toJson method handles JsonProcessingExceptions correctly.
    @JsonProperty("ExceptionalField")
    public String getExceptionalField() {
      throw new RuntimeException("Invoking a JsonProcessingException");
    }
  }

  @Test
  public void testJson() {
    UserScore albert = new UserScore("Albert", 100, "2020-10-04", "Easy");
    String expectedJson = "{\"name\":\"Albert\",\"score\":100,\"date\":\"2020-10-04\",\"difficulty\":\"Easy\"}";
    assertEquals(expectedJson, albert.toJson(), "JSON string is not correct");
  }
  
  @Test
  public void testJsonProcessingException() {
    UserScore albert = new ExceptionUserScore("Albert", 100, "2020-10-04", "Easy");
    String expectedJson = ""; // We are expecting to get a json processing exception, and that an empty string is returned.
    
    PrintStream orgError = System.err; // We don't want to print stacktrace to the console.
    ByteArrayOutputStream errorText = new ByteArrayOutputStream();
    System.setErr(new PrintStream(errorText));
    
    assertEquals(expectedJson, albert.toJson(), "JSON string is not correct");
    
    System.setErr(orgError);
  }

}