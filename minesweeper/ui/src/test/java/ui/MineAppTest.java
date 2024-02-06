package ui;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;

// New syntax for using JUnit 5, enables the use of the @Start annotation.
@ExtendWith(ApplicationExtension.class) 
public class MineAppTest {

  private Stage stage;

  @Start
  private void start(Stage stage) throws IOException {
    this.stage = stage;
    new MineApp().start(stage);
  }

  @Test
  public void testIconIsPresent() {
    assertFalse(stage.getIcons().isEmpty());
  }
}
