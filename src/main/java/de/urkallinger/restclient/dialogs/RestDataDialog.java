package de.urkallinger.restclient.dialogs;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.controller.RestDataDialogController;
import de.urkallinger.restclient.data.RestData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RestDataDialog {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestDataDialog.class);
	
	private Stage parentStage;
	private RestDataDialogController controller;
	private Optional<RestData> result = Optional.empty();

	private Stage stage;
	
	public RestDataDialog() {
	}

	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}
	
	public Optional<RestData> getResult() {
		return result;
	}
	
	public void showAndWait() {
		try {
			stage = new Stage();
			stage.setTitle("RESTClient-Configurations");
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/ui/RestDataDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			controller = loader.getController();

			Scene scene = new Scene(layout);
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			addKeyFilter(scene);
			stage.showAndWait();
			
			if(controller.isOk()) {
				result = Optional.of(controller.getSelectedEntry());
			}

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	private void addKeyFilter(Scene scene) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
			case ENTER:
				RestData restData = controller.getSelectedEntry();
				if(restData != null) {
					result = Optional.of(restData);
					stage.close();
				}
				break;
			case ESCAPE:
				stage.close();
				break;
			default: break;
			}
		});
	}
}
