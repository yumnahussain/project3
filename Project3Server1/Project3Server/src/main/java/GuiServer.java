import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;
import java.util.function.Consumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;


public class GuiServer extends Application {
	private Server serverConnection;
	private ListView<String> listOnlineUsers;
	private Map<String, String> userCredentials;
	private ListView<String> listItems;
	private ListView<String> listMetrics;
	private HBox lists;
	private ScheduledExecutorService executor;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		listItems = new ListView<String>();
		listOnlineUsers = new ListView<String>();
		listMetrics = new ListView<String>();
		userCredentials = new HashMap<>();

		// Create the main layout
		BorderPane pane = new BorderPane();
		pane.setPadding(new Insets(70));
		pane.setStyle("-fx-background-color: lightblue; -fx-font-family: 'serif';");

		// Create the metrics section
		VBox metricsBox = new VBox();
		metricsBox.setSpacing(10);
		metricsBox.setPadding(new Insets(10));

		// Create the main content area
		HBox mainContent = new HBox(listOnlineUsers, listItems, listMetrics);
		mainContent.setSpacing(10);

		pane.setCenter(mainContent);

		serverConnection = new Server(data -> Platform.runLater(() -> handleMessage(data)));

		startMonitoring();

		primaryStage.setScene(new Scene(pane, 800, 400));
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
				listOnlineUsers.getItems().setAll(data.onlineUsers);
				break;
			case NEWUSER:
				listOnlineUsers.getItems().add(data.username);
				listItems.getItems().add(data.username + " came online");
				break;
			case DISCONNECT:
				listOnlineUsers.getItems().remove(data.username);
				listItems.getItems().add(data.username + " went offline");
				break;
			case TEXT:
				listItems.getItems().add(data.username + " -> " + data.recipientUsername + ": " + data.message);
				break;
			case LOGIN:
				break;
			default:
				break;
		}
	}

	private void startMonitoring() {
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(() -> {
			Platform.runLater(() -> {
				// Get system metrics
//				double cpuUsage = ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
//				long totalMemory = Runtime.getRuntime().totalMemory();
//				long freeMemory = Runtime.getRuntime().freeMemory();
//				double memoryUsage = ((totalMemory - freeMemory) * 100.0) / totalMemory;

				// Update metrics list
				listMetrics.getItems().clear();
//				listMetrics.getItems().add(String.format("CPU Usage: %.2f%%", cpuUsage));
//				listMetrics.getItems().add(String.format("Memory Usage: %.2f%%", memoryUsage));
				listMetrics.getItems().add(String.format("Active Users: %d", listOnlineUsers.getItems().size()));
				listMetrics.getItems().add(String.format("Total Messages: %d", listItems.getItems().size()));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}
}



//import java.util.HashMap;
//import java.util.function.Consumer;
//import javafx.application.Application;
//import javafx.application.Platform;
//import javafx.event.EventHandler;
//import javafx.geometry.Insets;
//import javafx.scene.Scene;
//import javafx.scene.control.Label;
//import javafx.scene.control.ListView;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import javafx.stage.WindowEvent;
//import java.lang.management.ManagementFactory;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//
//public class GuiServer extends Application {
//	private Server serverConnection;
//	private ListView<String> listItems;
//	private ListView<String> listUsers;
//	private ListView<String> listMetrics;
//	private HBox lists;
//	private ScheduledExecutorService executor;
//
//	public static void main(String[] args) {
//		launch(args);
//	}
//
//	@Override
//	public void start(Stage primaryStage) throws Exception {
//		serverConnection = new Server(data -> {
//			Platform.runLater(() -> {
//				switch (data.type) {
//					case CHAT:
//						listItems.getItems().add(data.recipient + ": " + data.message);
//						break;
//					case NEWUSER:
//						listUsers.getItems().add(String.valueOf(data.recipient));
//						listItems.getItems().add(data.recipient + " has joined!");
//						break;
//					case DISCONNECT:
//						listUsers.getItems().remove(String.valueOf(data.recipient));
//						listItems.getItems().add(data.recipient + " has disconnected!");
//						break;
//				}
//			});
//		});
//
//		listItems = new ListView<String>();
//		listUsers = new ListView<String>();
//		listMetrics = new ListView<String>();
//
//		// Create the main layout
//		BorderPane pane = new BorderPane();
//		pane.setPadding(new Insets(70));
//		pane.setStyle("-fx-background-color: lightblue; -fx-font-family: 'serif';");
//
//		// Create the metrics section
//		VBox metricsBox = new VBox();
//		metricsBox.setSpacing(10);
//		metricsBox.setPadding(new Insets(10));
//
//		// Create the main content area
//		HBox mainContent = new HBox(listUsers, listItems, listMetrics);
//		mainContent.setSpacing(10);
//
//		pane.setCenter(mainContent);
//
//
//		// Start monitoring metrics
//		startMonitoring();
//
//		primaryStage.setScene(new Scene(pane, 800, 400));
//		primaryStage.setTitle("Server Monitor");
//		primaryStage.show();
//	}


//}
