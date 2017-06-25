package de.urkallinger.restclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.controller.ConfigurationController;
import de.urkallinger.restclient.controller.ConsoleController;
import de.urkallinger.restclient.data.DataManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

	private Stage stage;
	private Scene scene;

	private ConsoleHolder console;
	private ConfigurationHolder config;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		DataManager.createOrUpdateConfiguration();
		
		this.stage = primaryStage;
		this.stage.setTitle("RESTClient");

		Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
		primaryStage.getIcons().add(icon);


		BorderPane rootLayout = initRootLayout();
		config = initConfiguration();
		console = initConsole();
		
		rootLayout.setTop(config.pane);
		rootLayout.setBottom(console.pane);
		
		scene = new Scene(rootLayout);
		stage.setScene(scene);
		stage.show();
		
		scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch (event.getCode()) {
			case S:
				if(event.isControlDown()) {
					config.controller.save(event.isShiftDown());
				}
				break;
			case O:
				if(event.isControlDown()) {
					config.controller.load();
				}
			case C:
				if(event.isAltDown()) {
					console.controller.clear();
				}
				break;
			default: break;
			}
		
		});
	}

	private BorderPane initRootLayout() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/ui/RootLayout.fxml"));
		return loader.load();
	}

	private ConfigurationHolder initConfiguration() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/ui/Configuration.fxml"));
		
		ConfigurationHolder holder = new ConfigurationHolder();
		holder.pane = loader.load();
		holder.controller = loader.getController();

		return holder;
	}

	private ConsoleHolder initConsole() throws IOException {
		FXMLLoader loader = new FXMLLoader();

		loader.setLocation(getClass().getResource("/ui/Console.fxml"));
		
		ConsoleHolder holder = new ConsoleHolder();
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
}
