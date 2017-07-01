package de.urkallinger.restclient.dialogs;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.model.SaveDialogData;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SaveDialog extends Dialog<SaveDialogData> {
	public SaveDialog() {
		this.setTitle("Save configuration");
		Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
		
		Scene scene = this.getDialogPane().getScene();
		scene.getStylesheets().add(getClass().getResource("/css/GlobalFontSize.css").toExternalForm());
		
		Stage stage = (Stage) scene.getWindow();
		stage.getIcons().add(icon);
		
	}

	public Optional<SaveDialogData> showDialog() {
		// Set the button types.
		ButtonType btnOk = new ButtonType("OK", ButtonData.OK_DONE);
		this.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);

		Label lblProject = new Label("Project");
		
		Set<String> entries = DataManager.loadData().getRestDataMap().values().stream()
				.map(rs -> rs.getProject())
				.collect(Collectors.toSet());
		
		ComboBox<String> cbProject = new ComboBox<>();
		cbProject.getItems().addAll(entries);
		cbProject.setEditable(true);
		cbProject.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			if(event.getCode() == KeyCode.DOWN) cbProject.show();
		});

		Label lblName = new Label("Name");
		TextField txtName = new TextField();
		txtName.setPromptText("Configuration name");

		gridPane.add(lblProject, 0, 0);
		gridPane.add(cbProject, 1, 0);
		
		gridPane.add(lblName, 0, 1);
		gridPane.add(txtName, 1, 1);

		this.getDialogPane().setContent(gridPane);

		Platform.runLater(() -> cbProject.requestFocus());

		this.setResultConverter(dialogButton -> {
		    if (dialogButton == btnOk) {
		    	return new SaveDialogData(cbProject.getEditor().getText(), txtName.getText());
		    }
		    return null;
		});
		
		return super.showAndWait();
	}
}
