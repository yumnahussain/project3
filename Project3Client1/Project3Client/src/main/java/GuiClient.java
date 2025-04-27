import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.List;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;




public class GuiClient extends Application {
	private Client clientConnection;
	private Stage primaryStage;
	private String myUsername;
	private Circle[][] circles;

	private Scene welcomeScene, signupScene, loginScene, welcomeUserScene, chatScene, gameScene;
	private TextField usernameField, newUsernameField, chatInput;
	private PasswordField passwordField, newPasswordField;
	private Label welcomeUserLabel;
	private ComboBox<String> userSelector;
	private ListView<String> chatList;
	private Button sendButton;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		primaryStage = stage;
		clientConnection = new Client(data -> Platform.runLater(() -> handleMessage(data)));
		clientConnection.start();
		buildWelcomeScene();
		buildSignupScene();
		buildLoginScene();
		buildWelcomeUserScene();
		buildChatScene();
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Connect Four");
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
	}

	private void buildWelcomeScene() {
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(50));
		layout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");
		Label label = new Label("Welcome to Connect Four");
		label.setStyle("-fx-font-size: 20px;");
		Button signupButton = new Button("Sign Up");
		Button loginButton = new Button("Log In");
		signupButton.setOnAction(e -> primaryStage.setScene(signupScene));
		loginButton.setOnAction(e -> primaryStage.setScene(loginScene));
		layout.getChildren().addAll(label, signupButton, loginButton);
		welcomeScene = new Scene(layout, 400, 300);
	}

	private void buildSignupScene() {
		VBox layout = new VBox(15);
		layout.setPadding(new Insets(30));
		layout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");
		Label label = new Label("Sign Up");
		label.setStyle("-fx-font-size: 20px;");
		newUsernameField = new TextField();
		newUsernameField.setPromptText("Username");
		newPasswordField = new PasswordField();
		newPasswordField.setPromptText("Password");
		Button submit = new Button("Submit");
		Button back = new Button("Back");
		back.setOnAction(e -> primaryStage.setScene(welcomeScene));
		submit.setOnAction(e -> clientConnection.send(
				new Message(Message.MessageType.SIGNUP, newUsernameField.getText(), newPasswordField.getText())
		));
		layout.getChildren().addAll(label, newUsernameField, newPasswordField, submit, back);
		signupScene = new Scene(layout, 400, 300);
	}

	private void buildLoginScene() {
		VBox layout = new VBox(15);
		layout.setPadding(new Insets(30));
		layout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");
		Label label = new Label("Login");
		label.setStyle("-fx-font-size: 20px;");
		usernameField = new TextField();
		usernameField.setPromptText("Username");
		passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		Button submit = new Button("Submit");
		Button back = new Button("Back");
		back.setOnAction(e -> primaryStage.setScene(welcomeScene));
		submit.setOnAction(e -> clientConnection.send(
				new Message(Message.MessageType.LOGIN, usernameField.getText(), passwordField.getText())
		));
		layout.getChildren().addAll(label, usernameField, passwordField, submit, back);
		loginScene = new Scene(layout, 400, 300);
	}

	private void buildWelcomeUserScene() {
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(50));
		layout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");
		welcomeUserLabel = new Label();
		welcomeUserLabel.setStyle("-fx-font-size: 20px;");
		Button humanPlay = new Button("Play Against Human");
		Button aiPlay = new Button("Play Against AI");
		humanPlay.setOnAction(e -> primaryStage.setScene(chatScene));
		aiPlay.setOnAction(e -> primaryStage.setScene(chatScene));
		Button logOut = new Button("Log Out");
		logOut.setOnAction(e -> primaryStage.setScene(welcomeScene));
		layout.getChildren().addAll(welcomeUserLabel, humanPlay, aiPlay, logOut);
		welcomeUserScene = new Scene(layout, 400, 300);
	}

	private void buildChatScene() {
		userSelector = new ComboBox<>();
		userSelector.setPromptText("Select a user");
		chatList = new ListView<>();
		chatInput = new TextField();
		sendButton = new Button("Send");
		sendButton.setOnAction(e -> {
			String to = userSelector.getValue();
			if (to != null) {
				clientConnection.send(new Message(myUsername, to, chatInput.getText()));
				chatInput.clear();
			}
		});
		HBox controls = new HBox(10, userSelector, sendButton);
		VBox layout = new VBox(10, chatInput, controls, chatList);
		layout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");

		// Create game board grid
		GridPane gameBoard = new GridPane();
		gameBoard.setStyle("-fx-background-color: blue;");
		gameBoard.setPadding(new Insets(10));
		gameBoard.setHgap(5);
		gameBoard.setVgap(5);

		// Initialize the game board circles
		circles = new Circle[6][7];
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				Circle circle = new Circle(25);
				circle.setFill(Color.WHITE);
				circle.setStroke(Color.BLACK);
				circles[row][col] = circle;
				gameBoard.add(circle, col, row);

				// Add click handler for each column
				int column = col;
//				circle.setOnMouseClicked(e -> {
//					clientConnection.send(new Message(Message.MessageType.GAME_MOVE, column));
//				});
			}
		}

		layout.getChildren().add(gameBoard);

		HBox outerLayout = new HBox(10, new VBox(10), layout);
		chatScene = new Scene(outerLayout, 900, 300);
	}

	private void handleMessage(Message msg) {
		switch (msg.type) {
			case UPDATEUSERS:
				List<String> names = msg.onlineUsers;
				userSelector.getItems().setAll(names);
				break;
			case NEWUSER:
				chatList.getItems().add(msg.username + " came online");
				break;
			case DISCONNECT:
				chatList.getItems().add(msg.username + " went offline");
				break;
			case TEXT:
				chatList.getItems().add(msg.username + ": " + msg.message);
				break;
			case SIGNUP_RESPONSE:
				if ("SUCCESS".equals(msg.message)) {
					showInfo("Sign up successful; please log in.");
					primaryStage.setScene(loginScene);
				} else {
					showError("Username already taken.");
				}
				break;
			case LOGIN_RESPONSE:
				if ("SUCCESS".equals(msg.message)) {
					myUsername = usernameField.getText();
					welcomeUserLabel.setText("Welcome " + myUsername);
					primaryStage.setScene(welcomeUserScene);
				} else {
					showError("Invalid username or password.");
				}
				break;
//			case GAME_UPDATE:
//				if (circles != null) {
//					Platform.runLater(() -> {
//						for (int row = 0; row < 6; row++) {
//							for (int col = 0; col < 7; col++) {
//								if (msg.gameBoard[row][col] == 1) {
//									circles[row][col].setFill(Color.RED);
//								} else if (msg.gameBoard[row][col] == 2) {
//									circles[row][col].setFill(Color.YELLOW);
//								} else {
//									circles[row][col].setFill(Color.WHITE);
//								}
//							}
//						}
//					});
//				}
//				break;
			default:
				break;
		}
	}

	private void showError(String msg) {
		Alert alert = new Alert(Alert.AlertType.ERROR); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
	}

	private void showInfo(String msg) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION); alert.setHeaderText(null); alert.setContentText(msg); alert.showAndWait();
	}
}
