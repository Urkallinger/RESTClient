package de.urkallinger.restclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.communication.CommunicationHandler;
import de.urkallinger.restclient.controller.ConfigurationController;
import de.urkallinger.restclient.controller.ConsoleController;
import de.urkallinger.restclient.controller.ResponseController;
import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.RestData;
import de.urkallinger.restclient.data.RestDataType;
import de.urkallinger.restclient.dialogs.PropertiesDialog;
import de.urkallinger.restclient.dialogs.RestDataDialog;
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
		
		try {
			DataManager.createOrUpdateConfiguration();
		} catch (Exception e) {
			LOGGER.error("An error occurred while trying to create/update the saved data.");
			LOGGER.error(e.getMessage(), e);
		}
		
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
		scene.getStylesheets().add(getClass().getResource("/css/GlobalFontSize.css").toExternalForm());
		
		stage.setScene(scene);
		stage.show();
		
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
			case S:
				if(!event.isControlDown()) break;
				try {
					configHolder.controller.save(event.isShiftDown());
					setTitle(configHolder.controller.getRestData());
					
				} catch (Exception e) {
					LOGGER.error(e.getMessage());
				}
				break;
				
			case O:
				if(!event.isControlDown()) break;
				
				RestDataDialog openDialog = new RestDataDialog();
				openDialog.setAllowedType(RestDataType.REST_DATA);
				openDialog.setParentStage(stage);
				openDialog.showAndWait();
				openDialog.getResult().ifPresent(data -> {
					responseHolder.controller.clearContent();
					configHolder.controller.load((RestData) data);
					setTitle((RestData) data);
				});
				break;
				
			case C:
				if(!event.isAltDown()) break;
				consoleHolder.controller.clear();
				event.consume();
				break;
				
			case F:
				if(!event.isControlDown()) break;
				configHolder.controller.formatPayload();
				break;

			case H:
				if(!event.isAltDown()) break;
				configHolder.controller.addHeader();
				break;
				
			case P:
				if(!event.isControlDown()) break;
				PropertiesDialog propDialog = new PropertiesDialog();
				propDialog.showAndWait();
				
			case ENTER:
				if(!event.isAltDown()) break;
				sendRequest();
				event.consume();
				break;
				
			default: break;
			}
		});
	}
	
	private void setTitle(RestData data) {
		String title = "RESTClient" + " - " + configHolder.controller.getRestData().getName();
		stage.setTitle(title);
	}
	
	private void sendRequest() {
		Callback c = new Callback() {

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				String content = response.body().string();
				Platform.runLater(() -> responseHolder.controller.setText(content));
				if(response.isSuccessful()) {
					LOGGER.info(String.format("Response: %d - %s", response.code(), response.message()));
				} else {
					LOGGER.warn(String.format("Response: %d - %s", response.code(), response.message()));
				}
				response.body().close();
			}

			@Override
			public void onFailure(Call call, IOException e) {
				LOGGER.error(e.getMessage());
			}
		};
		
		try {
			CommunicationHandler.sendRequest(configHolder.controller.getRestData(), c);
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage());
		}
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
		holder.controller.setParentStage(stage);

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
