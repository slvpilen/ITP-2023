package ui;

import core.GameEngine;
import core.Tile;
import core.TileReadable;
import core.UserScore;
import core.settings.SettingsManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * This class is used to control the game page. It contains methods for handling user input, and
 * methods for updating the game view.
 */
public class GamePageController {

  @FXML
  private Label timeLabel;
  @FXML
  private Label gameStatusLabel;
  @FXML
  private Label flagsLeftLabel;
  @FXML
  private Label leaderBoardNameLabel;
  @FXML
  private Label feedbackLabel;
  @FXML
  private GridPane gameGrid;
  @FXML
  private TextField nameField;
  @FXML
  private Button sendToLeaderBoardButton;
  @FXML
  private VBox vbox;

  private RestRequest restRequest = new RestRequest("http://localhost:8080");
  protected GameEngine gameEngine;
  private Timeline timeline;
  private int[] currentSquare;

  /*
   * To access the imageView in the 'gameGrid' in O(1) time we need to store them in a 2D array.
   */
  private ImageView[][] imageViewList;

  /**
   * Initializes the game page. Creates a new game engine, and sets the stage size.
   * Also sets up the spacebar click, and updates the flags left label.

   * @throws IOException If the fxml file is not found.
   */
  @FXML
  public void initialize() throws IOException {
    this.gameEngine = new GameEngine();
    newGameGrid();
    spaceBarClickSetup();
    flagsLeftLabel.setText(String.valueOf(gameEngine.getFlagsLeft()));
    this.timeline = createTimeline();
    this.currentSquare = null;
    updateCollorTheme();
  }

  /**
   * Starts a new game. Both the game and the timer is reset.
   */
  @FXML
  public void resetGame() {
    gameEngine.resetGame();

    clearGameGrid();
    timeline.stop();
    updateTimeLabel();
    flagsLeftLabel.setText(String.valueOf(gameEngine.getFlagsLeft()));

    sendToLeaderBoardButton.setDisable(true);
    sendToLeaderBoardButton.setVisible(false);
    nameField.setVisible(false);
    nameField.setDisable(true);
    leaderBoardNameLabel.setVisible(false);
    feedbackLabel.setVisible(false);
    gameGrid.setDisable(false);
    gameStatusLabel.setText("");
  }

  /**
   * Switches to the highscore list page.
   * The highscores are fetched from the server,
   * and the scores you see are the ones for the current difficulty.

   * @param event The event that triggered the method call.
   * @throws IOException If the fxml file is not found.
   */
  @FXML
  public void switchToHighScoreList(ActionEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/HighscoreList.fxml"));
    Parent root = fxmlLoader.load();
    Node eventSource = (Node) event.getSource();
    Stage stage = (Stage) eventSource.getScene().getWindow();
    stage.setScene(new Scene(root));
    MineApp.setStageSize(stage, HighscoreListController.STAGE_WIDTH,
        HighscoreListController.STAGE_HEIGHT);
    stage.show();
  }

  /**
   * Switches to the settings page.

   * @param event The event that triggered the method call.
   * @throws IOException If the fxml file is not found.
   */
  @FXML
  public void switchToSettings(ActionEvent event) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/ui/Settings.fxml"));
    Parent root = fxmlLoader.load();
    Node eventSource = (Node) event.getSource();
    Stage stage = (Stage) eventSource.getScene().getWindow();
    stage.setScene(new Scene(root));
    MineApp.setStageSize(stage, SettingsController.STAGE_WIDTH, SettingsController.STAGE_HEIGHT);
    stage.show();
  }

  /**
   * Submits the current score to the highscore list.
   * Uses the RestRequest class to send a POST request to the server.
   */
  @FXML
  public void submitHighscore() {
    restRequest.writeToHighscore(new UserScore(nameField.getText(), gameEngine.getTime(),
        gameEngine.getDate(), SettingsManager.getGameDifficultyAsString()));

    feedbackLabel.setVisible(true);
    sendToLeaderBoardButton.setDisable(true);
    sendToLeaderBoardButton.setVisible(false);
    nameField.setVisible(false);
    nameField.setDisable(true);
    leaderBoardNameLabel.setVisible(false);
  }

  // Dynamic programming: For faster excecution store already importet images
  private HashMap<String, Image> imageMap = new HashMap<>();

  /**
   * Sets a new image for a given tile. If the image is not already imported, it will be imported.
   *
   * @param tile The tile to set the image for.
   */
  private void setNewImage(TileReadable tile) {
    String collorThemePath = SettingsManager.getThemeSettings().getTilePrefix();
    String tileImagePath = collorThemePath + tile.getRevealedImagePath();

    // Tiles with value zero has no image
    boolean tileIsZero = tileImagePath.contains("number0");
    if (tileIsZero) {
      imageMap.put(tileImagePath, null);
    }

    if (!imageMap.containsKey(tileImagePath)) {
      InputStream inputStream = Tile.class.getResourceAsStream(tileImagePath);
      imageMap.put(tileImagePath, new Image(inputStream));
    }

    ImageView imageView = imageViewList[tile.getX()][tile.getY()];
    imageView.setImage(imageMap.get(tileImagePath));
  }

  private void newGameGrid() {
    gameGrid.getChildren().clear();

    int gridWidth = SettingsManager.getGameDifficulty().getGridWidth();
    int gridHeight = SettingsManager.getGameDifficulty().getGridHeight();
    imageViewList = new ImageView[gridWidth][gridHeight];

    String collorThemePath = SettingsManager.getThemeSettings().getTilePrefix();
    Image squareImage = new Image(getClass()
        .getResourceAsStream("/images" + collorThemePath + "square.jpg"));

    for (int y = 0; y < gridHeight; y++) {
      for (int x = 0; x < gridWidth; x++) {
        ImageView newSquare = newSquare(squareImage, x, y);
        gameGrid.add(newSquare, x, y);
        imageViewList[x][y] = newSquare;
      }
    }
  }

  private ImageView newSquare(Image image, int x, int y) {
    ImageView imageView = new ImageView(image);

    // Set dimensions to square
    imageView.setFitWidth(SettingsManager.getSquareSize());
    imageView.setFitHeight(SettingsManager.getSquareSize());

    final int row = x;
    final int col = y;
    imageView.setOnMouseClicked(e -> {
      squareClicked(e, row, col);
      timeline.play();
    });
    // CurrentSquare gets updated when mouse hovers over a square
    imageView.setOnMouseEntered(e -> {
      currentSquare = new int[] { row, col };
    });
    imageView.setOnMouseExited(e -> {
      currentSquare = null;
    });
    return imageView;
  }

  private void squareClicked(MouseEvent e, int row, int col) {
    if (e.getButton().equals(MouseButton.PRIMARY)) {
      gameEngine.handleLeftClick(row, col);

    } else if (e.getButton().equals(MouseButton.SECONDARY)) {
      gameEngine.handleRightClick(row, col);
      flagsLeftLabel.setText("" + gameEngine.getFlagsLeft());
    }

    updateGameView();
  }

  /**
   * This method is used to initialize the spacebar click. The gridpane gets permanent focus,
   * and when the spacebar is clicked, the method spaceBarClicked() is called.
   */
  private void spaceBarClickSetup() {
    gameGrid.setFocusTraversable(true);
    gameGrid.requestFocus();
    gameGrid.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.SPACE) {
        spaceBarClicked();
      }
    });
  }

  /**
   * This method handles the logic when the spacebar is clicked.
   * All non-flagged neighboring tiles are clicked, unless the number
   * of flags around the tile is not equal to the number of bombs around the tile.
   */
  private void spaceBarClicked() {
    if (currentSquare == null) {
      return;
    }
    gameEngine.handleSpaceBarClick(currentSquare[0], currentSquare[1]);
    updateGameView();
  }

  private void updateGameView() {
    updateTiles();

    if (gameEngine.isGameWon()) {
      updateGameWon();
    }

    if (gameEngine.isGameLost()) {
      updateGameLost();
    }
  }

  private void updateGameWon() {
    timeline.stop();
    gameStatusLabel.setText("You win!");

    sendToLeaderBoardButton.setDisable(false);
    sendToLeaderBoardButton.setVisible(true);
    nameField.setVisible(true);
    nameField.setDisable(false);
    leaderBoardNameLabel.setVisible(true);
    gameGrid.setDisable(true);
  }

  private void updateGameLost() {
    timeline.stop();
    gameStatusLabel.setText("Game over!");
    gameGrid.setDisable(true);
  }

  private void updateTiles() {
    List<TileReadable> updatedTiles = gameEngine.getLatestUpdatedTiles();
    for (TileReadable tile : updatedTiles) {
      setNewImage(tile);
    }
  }

  private void clearGameGrid() {
    String mode = SettingsManager.getThemeSettings().getTilePrefix();
    Image squareImage = new Image(getClass().getResourceAsStream("/images" + mode + "square.jpg"));
    for (Node node : gameGrid.getChildren()) {
      ImageView iv = (ImageView) node;
      iv.setImage(squareImage);
    }
  }

  private Timeline createTimeline() {
    KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> updateTimeLabel());
    Timeline timeline = new Timeline(keyFrame);
    timeline.setCycleCount(Timeline.INDEFINITE);
    return timeline;
  }

  public void updateTimeLabel() {
    timeLabel.setText("" + gameEngine.getTime());
  }

  public void updateCollorTheme() {
    vbox.setStyle(SettingsManager.getThemeSettings().getBackgroundStyle());
    gameGrid.setStyle(SettingsManager.getThemeSettings().getBackgroundStyleGameGrid());
  }

  // This is for ui-test
  public TileReadable getTile(int columnIndex, int rowIndex) {
    return gameEngine.getTile(columnIndex, rowIndex);
  }

  // This is for ui-test
  public List<Tile> getNeighborTiles(int x, int y) {
    return gameEngine.getNeighborTiles(x, y);
  }

  // This is for ui-test:
  public boolean getStarted() {
    return gameEngine.isGameStarted();
  }

  // This is for ui-test
  public int getTime() {
    return gameEngine.getStopwatch().getTime();
  }

  // This is for ui-test
  public String getDate() {
    return gameEngine.getStopwatch().getDate();
  }

  // This is for ui-test
  protected void setRestRequest(RestRequest restRequest) {
    this.restRequest = restRequest;
  }

  protected GameEngine getGameEngine() {
    return gameEngine;
  }
}
