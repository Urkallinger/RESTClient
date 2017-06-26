package de.urkallinger.restclient.dialogs;

import java.io.IOException;

import de.urkallinger.restclient.controller.RestDataDialogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RestDataDialog {

	private Stage parentStage;

	public RestDataDialog() {
	}

	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
	}
	
	public void show() {
		try {
			Stage stage = new Stage();
			stage.setTitle("RESTClient-Configurations");
			stage.initOwner(parentStage);
			stage.initModality(Modality.APPLICATION_MODAL);

			Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
			stage.getIcons().add(icon);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/ui/RestDataDialog.fxml"));
			BorderPane layout = (BorderPane) loader.load();

			RestDataDialogController dialogController = loader.getController();

			Scene scene = new Scene(layout);
			stage.setMinWidth(layout.getMinWidth() + 50);
			stage.setMinHeight(layout.getMinHeight() + 50);
			stage.setScene(scene);
			stage.showAndWait();

		} catch (IOException e) {
			
		}
	}
}
