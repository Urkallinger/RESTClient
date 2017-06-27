package de.urkallinger.restclient.dialogs;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import de.urkallinger.restclient.controls.AutoCompleteTextField;
import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.model.SaveDialogData;
import javafx.application.Platform;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SaveDialog extends Dialog<SaveDialogData> {
	public SaveDialog() {
		this.setTitle("Save configuration");
		Image icon = new Image(getClass().getResourceAsStream("/images/AppIcon.png"));
		Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
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
		lblProject.setFont(new Font(14));
		AutoCompleteTextField txtProject = new AutoCompleteTextField();
		txtProject.setFont(new Font(14));
		Set<String> entries = DataManager.loadData().getRestDataMap().values().stream()
				.map(rs -> rs.getProject())
				.collect(Collectors.toSet());
		txtProject.getEntries().addAll(entries);
		txtProject.setPromptText("Projectname");
		
		Label lblName = new Label("Name");
		lblName.setFont(new Font(14));
		TextField txtName = new TextField();
		txtName.setFont(new Font(14));
		txtName.setPromptText("Configuration name");

		gridPane.add(lblProject, 0, 0);
		gridPane.add(txtProject, 1, 0);
		
		gridPane.add(lblName, 0, 1);
		gridPane.add(txtName, 1, 1);

		this.getDialogPane().setContent(gridPane);

		// Request focus on the username field by default.
		Platform.runLater(() -> txtProject.requestFocus());

		this.setResultConverter(dialogButton -> {
		    if (dialogButton == btnOk) {
		        return new SaveDialogData(txtProject.getText(), txtName.getText());
		    }
		    return null;
		});
		
		return super.showAndWait();
	}
}
