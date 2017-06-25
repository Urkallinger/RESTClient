package de.urkallinger.restclient.controller;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.Header;
import de.urkallinger.restclient.data.RestData;
import de.urkallinger.restclient.data.SaveData;
import de.urkallinger.restclient.utils.DragResizer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

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
	private GridPane headerGrid = new GridPane();
	
	@FXML
	private void initialize() {
		
		bindRestData(data);
		DragResizer.makeResizable(taPayload);
		
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
		data.getHeaders().forEach(h -> addHeader(h));
	}
	
	public void addHeader() {
		addHeader(null);
	}
	
	public void addHeader(Header header) {
		int rowCount = headerGrid.getChildren().stream().mapToInt(child -> GridPane.getRowIndex(child)).max().orElse(0);
		if(header == null) {
			header = new Header();
			data.addHeader(header);
		}
		
		Label label = new Label("Header");
		label.setFont(new Font(14));
		
		TextField txtName = new TextField();
		txtName.setFont(new Font(14));
		txtName.setPromptText("name");
		txtName.textProperty().bindBidirectional(header.getNameProperty());
		
		TextField txtValue = new TextField();
		txtValue.setFont(new Font(14));
		txtValue.setPromptText("value");
		txtValue.textProperty().bindBidirectional(header.getValueProperty());
		
		headerGrid.addRow(rowCount+1, label, txtName, txtValue);
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
		LOGGER.info(String.format("Configuration \"%s\" successfully saved.", data.getName()));
	}
	
	public void load() {
		// TODO: Eingabedialog nur temporär. Zukünftige schöner Dialog mit TableView
		Optional<String> result = showConfigNameDialog();
		if(result.isPresent()) {
			RestData data = DataManager.loadData().getRestData(result.get());
			if(data != null) {
				bindRestData(data);
			}
		}
	}
	
	public void formatPayload() {
		if(data.getPayload() == null) {
			return;
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(data.getPayload(), Object.class);
			data.setPayload(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
		} catch (JsonProcessingException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	public RestData getRestData() {
		return data;
	}
}
