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
	private int myPlayerNumber = 0;

	private Scene welcomeScene, signupScene, loginScene, welcomeUserScene, chatScene, gameScene, winScene, loseScene, drawScene;
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
		buildDrawScene();
		buildYellowScene();
		buildRedScene();
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Connect Four");
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> { Platform.exit(); System.exit(0); });
	}

	private void buildWelcomeScene() {
		VBox layout = new VBox(20);
		layout.setPadding(new Insets(50));
		layout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center;");
		Label label = new Label("Welcome to Connect Four");
		label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
		Button signupButton = new Button("Sign Up");
		Button loginButton = new Button("Log In");
		signupButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		loginButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		signupButton.setOnAction(e -> primaryStage.setScene(signupScene));
		loginButton.setOnAction(e -> primaryStage.setScene(loginScene));
		layout.getChildren().addAll(label, signupButton, loginButton);
		welcomeScene = new Scene(layout, 400, 300);
	}

	private void buildSignupScene() {
		VBox layout = new VBox(15);
		layout.setPadding(new Insets(30));
		layout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center;");
		Label label = new Label("Sign Up");
		label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
		newUsernameField = new TextField();
		newUsernameField.setPromptText("Username");
		newUsernameField.setStyle("-fx-padding: 8; -fx-font-size: 14px;");
		newPasswordField = new PasswordField();
		newPasswordField.setPromptText("Password");
		newPasswordField.setStyle("-fx-padding: 8; -fx-font-size: 14px;");
		Button submit = new Button("Submit");
		Button back = new Button("Back");
		submit.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
		back.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
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
		layout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center;");
		Label label = new Label("Login");
		label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
		usernameField = new TextField();
		usernameField.setPromptText("Username");
		usernameField.setStyle("-fx-padding: 8; -fx-font-size: 14px;");
		passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		passwordField.setStyle("-fx-padding: 8; -fx-font-size: 14px;");
		Button submit = new Button("Submit");
		Button back = new Button("Back");
		submit.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
		back.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 20;");
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
		layout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center;");
		welcomeUserLabel = new Label();
		welcomeUserLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
		Button humanPlay = new Button("Play Against Human");
		Button aiPlay = new Button("Play Against AI");
		Button logOut = new Button("Log Out");
		humanPlay.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		aiPlay.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		logOut.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		humanPlay.setOnAction(e -> {
			clearBoard();
			clientConnection.send(new Message(Message.MessageType.REQUEST_GAME));
			primaryStage.setScene(chatScene);
			chatList.getItems().add("Waiting for opponent...");
		});
		aiPlay.setOnAction(e -> {
			clearBoard();
			clientConnection.send(new Message(Message.MessageType.REQUEST_AI_GAME));
			primaryStage.setScene(chatScene);
			chatList.getItems().add("Starting game against AI...");
		});
		logOut.setOnAction(e -> primaryStage.setScene(welcomeScene));
		layout.getChildren().addAll(welcomeUserLabel, humanPlay, aiPlay, logOut);
		welcomeUserScene = new Scene(layout, 400, 300);
	}

	private void clearBoard() {
		if (circles != null) {
			for (int row = 0; row < 6; row++) {
				for (int col = 0; col < 7; col++) {
					circles[row][col].setFill(Color.WHITE);
				}
			}
		}
	}

	private void buildRedScene() {
		VBox winLayout = new VBox(20);
		winLayout.setPadding(new Insets(50));
		winLayout.setPrefSize(400, 300);
		winLayout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center;");

		Label winLabel = new Label("RED WINS!!!");
		winLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

		Button playAgainButton = new Button("Play Again");
		Button exitButton = new Button("Exit Game");

		playAgainButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		exitButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");

		playAgainButton.setOnAction(e -> {
			clearBoard();
			clientConnection.send(new Message(Message.MessageType.REQUEST_GAME));
			primaryStage.setScene(chatScene);
			chatList.getItems().add("Waiting for opponent...");
		});
		exitButton.setOnAction(e -> primaryStage.setScene(welcomeUserScene));

		winLayout.getChildren().addAll(winLabel, playAgainButton, exitButton);
		winScene = new Scene(winLayout);
	}

	private void buildYellowScene() {
		VBox loseLayout = new VBox(20);
		loseLayout.setPadding(new Insets(50));
		loseLayout.setPrefSize(400, 300);
		loseLayout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center;");

		Label loseLabel = new Label("YELLOW WINS!!!");
		loseLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #f1c40f;");

		Button playAgainButton1 = new Button("Play Again");
		Button exitButton1 = new Button("Exit Game");

		playAgainButton1.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		exitButton1.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");

		playAgainButton1.setOnAction(e -> {
			clearBoard();
			clientConnection.send(new Message(Message.MessageType.REQUEST_GAME));
			primaryStage.setScene(chatScene);
			chatList.getItems().add("Waiting for opponent...");
		});
		exitButton1.setOnAction(e -> primaryStage.setScene(welcomeUserScene));

		loseLayout.getChildren().addAll(loseLabel, playAgainButton1, exitButton1);
		loseScene = new Scene(loseLayout);
	}

	private void buildDrawScene() {
		VBox tieLayout = new VBox(20);
		tieLayout.setPadding(new Insets(50));
		tieLayout.setPrefSize(400, 300);
		tieLayout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center;");

		Label tieLabel = new Label("IT IS A TIE!!!");
		tieLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

		Button playAgainButton2 = new Button("Play Again");
		Button exitButton2 = new Button("Exit Game");

		playAgainButton2.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");
		exitButton2.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 20;");

		playAgainButton2.setOnAction(e -> {
			clearBoard();
			clientConnection.send(new Message(Message.MessageType.REQUEST_GAME));
			primaryStage.setScene(chatScene);
			chatList.getItems().add("Waiting for opponent...");
		});
		exitButton2.setOnAction(e -> primaryStage.setScene(welcomeUserScene));

		tieLayout.getChildren().addAll(tieLabel, playAgainButton2, exitButton2);
		drawScene = new Scene(tieLayout);
	}

	private void buildChatScene() {
		userSelector = new ComboBox<>();
		userSelector.setPromptText("Select a user");
		userSelector.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
		chatList = new ListView<>();
		chatList.setStyle("-fx-font-size: 14px; -fx-background-color: white;");
		chatInput = new TextField();
		chatInput.setPromptText("Type your message here...");
		chatInput.setStyle("-fx-font-size: 14px; -fx-padding: 8;");
		sendButton = new Button("Send");
		sendButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8 15;");
		sendButton.setOnAction(e -> {
			String to = userSelector.getValue();
			if (to != null) {
				clientConnection.send(new Message(myUsername, to, chatInput.getText()));
				chatInput.clear();
			}
		});
		HBox controls = new HBox(10, userSelector, sendButton);
		VBox layout = new VBox(10, chatInput, controls, chatList);
		layout.setStyle("-fx-background-color: #f0f8ff; -fx-alignment: center; -fx-padding: 20;");

		// Create game board grid
		GridPane gameBoard = new GridPane();
		gameBoard.setStyle("-fx-background-color: #2980b9; -fx-padding: 20; -fx-border-radius: 10; -fx-background-radius: 10;");
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
				circle.setOnMouseClicked((MouseEvent e) -> {
					clientConnection.send(new Message(Message.MessageType.GAME_MOVE, myUsername, column));
				});
			}
		}

		HBox outerLayout = new HBox(30, gameBoard, layout);
		outerLayout.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-background-color: #f0f8ff;");
		chatScene = new Scene(outerLayout, 800, 400);
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
			case GAME_START:
				myPlayerNumber = msg.playerNumber;
				System.out.println("myPlayerNumber: " + myPlayerNumber);
				if (myPlayerNumber == 0) {
					chatList.getItems().add("Game started! You are red.");
				}else{
					chatList.getItems().add("Game started! You are yellow.");
				}

				break;
			case GAME_UPDATE:
				if (circles != null && msg.gameBoard != null) {
					Platform.runLater(() -> {
						System.out.println("Received game update with board:");
						for (int row = 0; row < 6; row++) {
							for (int col = 0; col < 7; col++) {
								System.out.print(msg.gameBoard[row][col] + " ");
								if (msg.gameBoard[row][col] == 1) {
									circles[row][col].setFill(Color.RED);
								} else if (msg.gameBoard[row][col] == 2) {
									circles[row][col].setFill(Color.YELLOW);
								} else {
									circles[row][col].setFill(Color.WHITE);
								}
							}
							System.out.println();
						}
					});
				}
				break;
			case GAME_OVER:
				if (msg.playerNumber == 1) {
					primaryStage.setScene(loseScene);
				} else {
					primaryStage.setScene(winScene);
				}
				
				break;
			case GAME_MOVE:
				chatList.getItems().add("Player made a move in column " + msg.column);
				break;
			case REQUEST_GAME:
				chatList.getItems().add("Game request sent");
				break;
			case INVALID_MOVE:
				showError("Invalid move: " + msg.message);
				break;
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
