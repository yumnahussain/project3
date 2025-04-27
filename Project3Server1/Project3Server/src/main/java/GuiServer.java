import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import java.util.function.Consumer;

public class GuiServer extends Application {
	private Server serverConnection;
	private ListView<String> listUsers;
	private ListView<String> listItems;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		listUsers = new ListView<>();
		listItems = new ListView<>();
		HBox lists = new HBox(10, listUsers, listItems);

		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(20));
		pane.setCenter(lists);
		pane.setStyle("-fx-background-color: lightblue; -fx-font-family: 'serif';");

		serverConnection = new Server(data -> Platform.runLater(() -> handleMessage(data)));

		primaryStage.setScene(new Scene(pane, 500, 400));
		primaryStage.setTitle("Server");
		primaryStage.show();
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private void handleMessage(Message data) {
		switch (data.type) {
			case UPDATEUSERS:
				listUsers.getItems().setAll(data.onlineUsers);
				break;
			case NEWUSER:
				listUsers.getItems().add(data.username);
				listItems.getItems().add(data.username + " came online");
				break;
			case DISCONNECT:
				listUsers.getItems().remove(data.username);
				listItems.getItems().add(data.username + " went offline");
				break;
			case TEXT:
				listItems.getItems().add(data.username + " → " + data.recipientUsername + ": " + data.message);
				break;
			case SIGNUP_RESPONSE:
				listItems.getItems().add("Signup response: " + data.message);
				break;
			case LOGIN_RESPONSE:
				listItems.getItems().add("Login response: " + data.message);
				break;
			case GAME_START:
				listItems.getItems().add("Game started for player " + data.playerNumber);
				break;
			case GAME_UPDATE:
				listItems.getItems().add("Game board updated");
				break;
			case GAME_OVER:
				listItems.getItems().add("Game over! Player " + data.playerNumber + " wins!");
				break;
			case GAME_MOVE:
				listItems.getItems().add("Player made a move in column " + data.column);
				break;
			case REQUEST_GAME:
				listItems.getItems().add("Player requested a game");
				break;
			case INVALID_MOVE:
				listItems.getItems().add("Invalid move attempted: " + data.message);
				break;
			default:
				break;
		}
	}
}
