package de.urkallinger.restclient.dialogs;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class YesNoAlert extends Alert{
	public YesNoAlert(String headerText, String contentText) {
		super(Alert.AlertType.CONFIRMATION, contentText, ButtonType.YES, ButtonType.NO);
		
		this.setHeaderText(headerText);
		
		Button okButton = (Button) this.getDialogPane().lookupButton(ButtonType.YES);
		okButton.setDefaultButton(false);

		EventHandler<KeyEvent> fireOnEnter = event -> {
			if (KeyCode.ENTER.equals(event.getCode()) && event.getTarget() instanceof Button) {
				((Button) event.getTarget()).fire();
			}
		};

		DialogPane dialogPane = this.getDialogPane();
		dialogPane.getButtonTypes().stream().map(dialogPane::lookupButton)
				.forEach(btn -> btn.addEventHandler(KeyEvent.KEY_PRESSED, fireOnEnter));

		this.getDialogPane().getStylesheets().add(getClass().getResource("/css/GlobalFontSize.css").toExternalForm());
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/AppIcon.png")));
	}
}
