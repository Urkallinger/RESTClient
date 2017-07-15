package de.urkallinger.restclient.dialogs;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.controller.PropertiesDialogController;
import de.urkallinger.restclient.data.Property;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PropertiesDialog {

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesDialog.class);
	
	private Stage parentStage;
	private PropertiesDialogController controller;
	private Stage stage;
	
	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}

	public Optional<Property> showAndWait() {
		try {
			stage = new Stage();
			stage.setTitle("RESTClient-Properties");
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/ui/PropertiesDialog.fxml"));
			AnchorPane layout = (AnchorPane) loader.load();
			controller = loader.getController();

			Scene scene = new Scene(layout);
			scene.getStylesheets().add(getClass().getResource("/css/GlobalFontSize.css").toExternalForm());
			
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			addKeyFilter(scene);
			stage.showAndWait();

			return controller.getSelection();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return Optional.empty();
	}
	
	private void addKeyFilter(Scene scene) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch (event.getCode()) {
			case ENTER:
				if(!event.isControlDown()) break;
				controller.handleOk();
				event.consume();
				break;
			case ESCAPE:
				controller.handleCancel();
				event.consume();
				break;

			default: break;
			}
		});
	}
}
