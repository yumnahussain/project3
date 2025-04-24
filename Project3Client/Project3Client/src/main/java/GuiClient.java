import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiClient extends Application {

	TextField c1;
	Button b1;
	HashMap<String, Scene> sceneMap;
	VBox clientBox;
	Client clientConnection;

	HBox fields;

	ComboBox<Integer> listUsers;
	ListView<String> listItems;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		clientConnection = new Client(data -> {
			Platform.runLater(() -> {
				switch (data.type) {
					case NEWUSER:
						listUsers.getItems().add(data.recipient);
						listItems.getItems().add(data.recipient + " has joined!");
						break;
					case DISCONNECT:
						listUsers.getItems().remove(data.recipient);
						listItems.getItems().add(data.recipient + " has disconnected!");
						break;
					case TEXT:
						listItems.getItems().add(data.recipient + ": " + data.message);
						break;
//					case UPDATEUSERS:
//						listUsers.getItems().clear();
//						listUsers.getItems().add(-1);
//						for(int rec: data.clients){
//							listUsers.getItems().add(rec);
//						}
//						break;
				}
			});
		});

		clientConnection.start();

		// texting screen setup
		listUsers = new ComboBox<>();
		listUsers.getItems().add(-1);
		listUsers.setValue(-1);
		listItems = new ListView<>();

		c1 = new TextField();
		b1 = new Button("Send");
		fields = new HBox(listUsers, b1);
		b1.setOnAction(e -> {
			clientConnection.send(new Message(listUsers.getValue(), c1.getText()));
			c1.clear();
		});

		clientBox = new VBox(10, c1, fields, listItems);
		clientBox.setStyle("-fx-background-color: lightblue; -fx-alignment: center;" + "-fx-font-family: 'serif';");
		Scene mainScene = new Scene(clientBox, 400, 300);

		// login screen
		VBox loginLayout = new VBox(15);
		loginLayout.setPadding(new Insets(30));
		loginLayout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");

		Label loginLabel = new Label("Login");
		loginLabel.setStyle("-fx-font-size: 20px;");

		TextField usernameField = new TextField();
		usernameField.setPromptText("Username");

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");

		Button loginSubmit = new Button("Submit");
		loginSubmit.setOnAction(e -> primaryStage.setScene(mainScene)); // need to get logic working later

		loginLayout.getChildren().addAll(loginLabel, usernameField, passwordField, loginSubmit);
		Scene loginScene = new Scene(loginLayout, 400, 300);

		// sign up screen
		VBox signupLayout = new VBox(15);
		signupLayout.setPadding(new Insets(30));
		signupLayout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");

		Label signupLabel = new Label("Sign Up");
		signupLabel.setStyle("-fx-font-size: 20px;");

		TextField newUsernameField = new TextField();
		newUsernameField.setPromptText("Set Username");

		PasswordField newPasswordField = new PasswordField();
		newPasswordField.setPromptText("Set Password");

		Button signupSubmit = new Button("Submit");
		signupSubmit.setOnAction(e -> primaryStage.setScene(mainScene)); // get sign up logic to work later

		signupLayout.getChildren().addAll(signupLabel, newUsernameField, newPasswordField, signupSubmit);
		Scene signupScene = new Scene(signupLayout, 400, 300);

		// welcome to game screen
		VBox welcomeLayout = new VBox(20);
		welcomeLayout.setPadding(new Insets(50));
		welcomeLayout.setPrefSize(400, 300);
		welcomeLayout.setStyle("-fx-background-color: lightblue; -fx-alignment: center;");

		Label welcomeLabel = new Label("Welcome to Connect Four");
		welcomeLabel.setStyle("-fx-font-size: 20px;");

		Button signupButton = new Button("Sign Up");
		Button loginButton = new Button("Log In");

		signupButton.setOnAction(e -> primaryStage.setScene(signupScene));
		loginButton.setOnAction(e -> primaryStage.setScene(loginScene));

		welcomeLayout.getChildren().addAll(welcomeLabel, signupButton, loginButton);
		Scene welcomeScene = new Scene(welcomeLayout);

		// show the welcome screen first thing
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle("Connect Four");
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}
}

