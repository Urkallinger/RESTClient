package de.urkallinger.restclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.communication.CommunicationHandler;
import de.urkallinger.restclient.controller.ConfigurationController;
import de.urkallinger.restclient.controller.ConsoleController;
import de.urkallinger.restclient.controller.ResponseController;
import de.urkallinger.restclient.data.DataManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainApp extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

	private Stage stage;
	private Scene scene;

	private ConsoleHolder consoleHolder;
	private ConfigurationHolder configHolder;
	private ResponseHolder responseHolder;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		DataManager.createOrUpdateConfiguration();
		
		this.stage = primaryStage;
		this.stage.setTitle("RESTClient");

		Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
		primaryStage.getIcons().add(icon);


		BorderPane rootLayout = initRootLayout();
		configHolder = initConfiguration();
		consoleHolder = initConsole();
		responseHolder = initResponse();
		
		rootLayout.setTop(configHolder.pane);
		rootLayout.setBottom(consoleHolder.pane);
		rootLayout.setCenter(responseHolder.pane);
		
		scene = new Scene(rootLayout);
		stage.setScene(scene);
		stage.show();
		
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
			case S:
				if(event.isControlDown()) {
					configHolder.controller.save(event.isShiftDown());
				}
				break;
			case O:
				if(event.isControlDown()) {
					configHolder.controller.load();
				}
				break;
			case C:
				if(event.isAltDown()) {
					consoleHolder.controller.clear();
					event.consume();
				}
				break;
			case F:
				if(event.isControlDown()) {
					configHolder.controller.formatPayload();
				}
				break;
			case ENTER:
				if(event.isAltDown()) {
					sendRequest();
					event.consume();
				}
				break;
			default: break;
			}
		});
	}
	
	private void sendRequest() {
		Callback c = new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				 String content = response.body().string();
				Platform.runLater(() -> responseHolder.controller.setText(content)); 
				LOGGER.info(String.format("response: %d - %s", response.code(), response.message()));
			}

			@Override
			public void onFailure(Call call, IOException e) {
				LOGGER.error(e.getMessage());
			}
		};
		
		CommunicationHandler.sendRequest(configHolder.controller.getRestData(), c);
	}

	private BorderPane initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/ui/RootLayout.fxml"));
		return loader.load();
	}

	private ConfigurationHolder initConfiguration() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/ui/ConfigurationPane.fxml"));
		
		ConfigurationHolder holder = new ConfigurationHolder();
		holder.pane = loader.load();
		holder.controller = loader.getController();

		return holder;
	}

	private ConsoleHolder initConsole() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/ui/ConsolePane.fxml"));
		
		ConsoleHolder holder = new ConsoleHolder();
		holder.pane = loader.load();
		holder.controller = loader.getController();
		
		return holder;
	}
	
	private ResponseHolder initResponse() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/ui/ResponsePane.fxml"));
		
		ResponseHolder holder = new ResponseHolder();
		holder.pane = loader.load();
		holder.controller = loader.getController();
		
		return holder;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	private static class ConsoleHolder {
		public Pane pane;
		public ConsoleController controller;
	}
	
	private static class ConfigurationHolder {
		public Pane pane;
		public ConfigurationController controller;
	}
	
	private static class ResponseHolder {
		public Pane pane;
		public ResponseController controller;
	}
}
