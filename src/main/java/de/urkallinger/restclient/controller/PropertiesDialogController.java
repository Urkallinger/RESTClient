package de.urkallinger.restclient.controller;

import java.util.Map;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.Property;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PropertiesDialogController {
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
	private void initialize() {
		Image imgRes = new Image(getClass().getResourceAsStream("/images/add.png"));
		btnAdd.setGraphic(new ImageView(imgRes));
		
		colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		colValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());
		
		colName.setCellFactory(TextFieldTableCell.<Property>forTableColumn());
		colValue.setCellFactory(TextFieldTableCell.<Property>forTableColumn());
		
		Map<String, Property> properties = DataManager.loadData().getProperties();
		
		properties.forEach((k, v) -> table.getItems().add(v));
	}
	
	@FXML
	public void handleAdd() {
		
	}
	
	@FXML
	public void handleOk() {
		
	}
}
