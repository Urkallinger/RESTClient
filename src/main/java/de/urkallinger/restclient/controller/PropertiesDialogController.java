package de.urkallinger.restclient.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.Property;
import de.urkallinger.restclient.data.SaveData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class PropertiesDialogController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesDialogController.class);
	
	@FXML
	TableView<Property> table = new TableView<>();
	@FXML
	TableColumn<Property, String> colName = new TableColumn<>();
	@FXML
	TableColumn<Property, String> colValue = new TableColumn<>();
	@FXML
	TextField txtName = new TextField();
	@FXML
	TextField txtValue = new TextField();
	@FXML
	Button btnAdd = new Button();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();

	private Optional<Property> selection = Optional.empty();
	
	@FXML
	private void initialize() {
		Image imgRes = new Image(getClass().getResourceAsStream("/images/add.png"));
		btnAdd.setGraphic(new ImageView(imgRes));
		
		colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		colValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
		
		colName.setCellFactory(TextFieldTableCell.<Property>forTableColumn());
		colValue.setCellFactory(TextFieldTableCell.<Property>forTableColumn());
		
		Map<String, Property> properties = DataManager.loadData().getProperties();
		
		properties.values().stream()
			.sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
			.forEach(p -> table.getItems().add(p));
		
		Platform.runLater(() -> setFocusOnTable());
	}
	
	@FXML
	public void handleAdd() {
		if(txtName.getText() == null  || txtName.getText().isEmpty()) {
			LOGGER.warn("Property name can not be empty!");
			return;
		}
		if(txtValue.getText() == null  || txtValue.getText().isEmpty()) {
			LOGGER.warn("Property value can not be empty!");
			return;
		}
		
		Property prop = new Property();
		prop.setName(txtName.getText());
		prop.setValue(txtValue.getText());
		
		SaveData saveData = DataManager.loadData();
		if(!saveData.getProperties().containsKey(prop.getName())) {
			saveData.getProperties().put(prop.getName(), prop);
			DataManager.saveData(saveData);
			table.getItems().add(prop);
			LOGGER.info(String.format("new property added (%s: %s)", prop.getName(), prop.getValue()));
		} else {
			String msg = String.format("Property \"%s\" already exists. "
					+ "Edit the value of the existing property in the table or choose another name.",  prop.getName());
			LOGGER.error(msg);
		}
	}
	
	@FXML
	public void handleOk() {
		selection = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
		((Stage) btnOk.getScene().getWindow()).close();
	}
	
	@FXML
	public void handleCancel() {
		((Stage) btnCancel.getScene().getWindow()).close();
	}
	
	@FXML
	public void handleTableKeyEvent(KeyEvent event) {
		Optional<Property> item;
		switch (event.getCode()) {
		case DELETE:
			item = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
			item.ifPresent(property -> {
				table.getItems().remove(property);
				SaveData saveData = DataManager.loadData();
				saveData.getProperties().remove(property.getName());
				DataManager.saveData(saveData);
			});
			break;

		default:
			break;
		}
	}
	
	public Optional<Property> getSelection() {
		return selection;
	}
	
	private void setFocusOnTable() {
		table.getSelectionModel().select(0);
		table.getFocusModel().focus(0);
		table.requestFocus();
	}
}
