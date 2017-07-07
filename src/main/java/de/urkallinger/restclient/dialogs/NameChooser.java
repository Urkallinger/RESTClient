package de.urkallinger.restclient.dialogs;

import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class NameChooser extends TextInputDialog {

	public NameChooser(String title, String text) {
		this.setTitle(title);
		this.setHeaderText(text);
		
		Scene scene = this.getDialogPane().getScene();
		scene.getStylesheets().add(getClass().getResource("/css/GlobalFontSize.css").toExternalForm());
		
		Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
		Stage stage = (Stage) scene.getWindow();
		stage.getIcons().add(icon);
	}
}
