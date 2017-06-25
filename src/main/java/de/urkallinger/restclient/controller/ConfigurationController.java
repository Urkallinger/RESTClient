package de.urkallinger.restclient.controller;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.RestData;
import de.urkallinger.restclient.data.SaveData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;

public class ConfigurationController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);
	
	private RestData data = new RestData();
	
	@FXML
	private TextField txtHost = new TextField();
	@FXML
	private TextField txtPath = new TextField();
	@FXML
	private ComboBox<String> cbHttpMethod = new ComboBox<>();
	@FXML
	private TextArea taPayload = new TextArea();
	
	@FXML
	private void initialize() {
		
		bindRestData(data);
		
		taPayload.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch(event.getCode()) {
			case TAB:
				String s = StringUtils.repeat(' ', 4);
	            taPayload.insertText(taPayload.getCaretPosition(), s);
	            event.consume();
				break;
				
			default:
				break;
			}
		});
	}
	
	private Optional<String> showConfigNameDialog() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Configuration name");
		dialog.setHeaderText("Please insert the name for the configuration.");
		
		final Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		okButton.addEventFilter(ActionEvent.ACTION, ae -> {
		    if (dialog.getEditor().getText().isEmpty()) {
		    	LOGGER.error("Name can not be empty!");
		        ae.consume(); //not valid
		    }
		});
		
		return dialog.showAndWait();
	}
	
	private void bindRestData(RestData data) {
		this.data = data;
		txtHost.textProperty().bindBidirectional(data.getHostProperty());
		txtPath.textProperty().bindBidirectional(data.getPathProperty());
		taPayload.textProperty().bindBidirectional(data.getPayloadProperty());
		cbHttpMethod.valueProperty().bindBidirectional(data.getHttpMethodProperty());
	}
	
	public void save(boolean saveAsNew) {
		if(saveAsNew || data.getName() == null || data.getName().isEmpty()) {
			Optional<String> result = showConfigNameDialog();
			if(result.isPresent()) {
				data.setName(result.get());
			} else {
				return;
			}
		}
		
		SaveData saveData = DataManager.loadData();
		saveData.addRestData(data.getName(), data);
		DataManager.saveData(saveData);
		LOGGER.info("Configuration successfully saved.");
	}
	
	public void load() {
		Optional<String> result = showConfigNameDialog();
		if(result.isPresent()) {
			RestData data = DataManager.loadData().getRestData(result.get());
			if(data != null) {
				bindRestData(data);
			}
		}
	}
}
