package de.urkallinger.restclient.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.Header;
import de.urkallinger.restclient.data.RestData;
import de.urkallinger.restclient.data.RestDataBase;
import de.urkallinger.restclient.data.RestDataContainer;
import de.urkallinger.restclient.data.RestDataType;
import de.urkallinger.restclient.data.SaveData;
import de.urkallinger.restclient.dialogs.NameChooser;
import de.urkallinger.restclient.dialogs.RestDataDialog;
import de.urkallinger.restclient.dialogs.YesNoAlert;
import de.urkallinger.restclient.utils.Constants;
import de.urkallinger.restclient.utils.DragResizer;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
	
	private Stage parentStage;
	
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
	
	private void bindRestData(RestData data) {
		this.data = data;
		txtHost.textProperty().bindBidirectional(data.getHostProperty());
		txtPath.textProperty().bindBidirectional(data.getPathProperty());
		taPayload.textProperty().bindBidirectional(data.getPayloadProperty());
		cbHttpMethod.valueProperty().bindBidirectional(data.getHttpMethodProperty());
		
		clearHeaderGrid();
		data.getHeaders().forEach(h -> addHeader(h));
	}
	
	private void clearHeaderGrid() {
		headerGrid.getChildren().clear();
	}
	
	private Optional<ButtonType> showOverrideDialog(String name) {
		String headerText = String.format("A configuration with the name \"%s\" already exists.", name);
		String contentText = "Do you want do override it?"; 
		return new YesNoAlert(headerText, contentText).showAndWait();
	}
	
	public void setParentStage(Stage parentStage) {
		this.parentStage = parentStage;
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
		
		final TextField txtName = new TextField();
		txtName.setPromptText("name");
		txtName.textProperty().bindBidirectional(header.getNameProperty());
		TextFields.bindAutoCompletion(txtName, Constants.HTTP_HEADER);
		
		final TextField txtValue = new TextField();
		txtValue.setPromptText("value");
		txtValue.textProperty().bindBidirectional(header.getValueProperty());
		
		Button btnDelete = new Button();
		btnDelete.setOnAction(event -> {
			Button btn = (Button) event.getTarget();
			final int row = GridPane.getRowIndex(btn);
			List<Node> toDelete = headerGrid.getChildren().stream()
					.filter(child -> GridPane.getRowIndex(child) == row)
					.collect(Collectors.toList());
			data.getHeaders().remove(row - 1);
			headerGrid.getChildren().removeAll(toDelete);
		});
		
		btnDelete.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/delete.png"))));
		
		headerGrid.addRow(rowCount+1, label, txtName, txtValue, btnDelete);
	}
	
	public void save(boolean saveAsNew) throws Exception {
		
		if(saveAsNew || data.getName() == null || data.getName().isEmpty()) {
			RestDataDialog containerChooser = new RestDataDialog();
			containerChooser.setAllowedType(RestDataType.CONTAINER);
			containerChooser.setParentStage(parentStage);
			containerChooser.showAndWait();
			
			if (!containerChooser.getResult().isPresent()) {
				// cancel clicked
				return;
			}
			
			NameChooser nameChooser = new NameChooser("Configuration name", "Choose a name for the new configuration.");
			Optional<String> result = nameChooser.showAndWait();
			if (!result.isPresent()){
			    // cancel clicked
				return;
			}

			RestDataContainer c = (RestDataContainer) containerChooser.getResult().get();
			String name = result.get();
			
			Optional<RestDataBase> other = c.getChildren().stream().filter(x -> x.getName().equals(name)).findFirst();
			
			if(other.isPresent()) {
				Optional<ButtonType> override = showOverrideDialog(name);
				if(override.isPresent() && override.get() == ButtonType.YES) {
					data.setId(other.get().getId());
				} else {
					// do not override
					return;
				}
			} else {
				data.setId(UUID.randomUUID());
			}
			
			data.setName(name);
			
			SaveData saveData = DataManager.loadData();
			if(saveData.addRestData(data, c.getId())) {
				DataManager.saveData(saveData);
			} else {
				throw new Exception("Could not save configuration.");
			}
			
		} else {
			SaveData saveData = DataManager.loadData();
			if(saveData.updateRestData(data)) {
				DataManager.saveData(saveData);
			} else {
				throw new Exception("Could not save configuration. Try save as new configuration (CTRL+SHIFT+S)."); 
			}
		}
		LOGGER.info(String.format("Configuration \"%s\" successfully saved.", data.getName()));
	}
	
	public void load(RestData data) {
		try {
			bindRestData(data);
			LOGGER.info(String.format("Configuration \"%s\" successfully loaded.", data.getName()));
		} catch (Exception e) {
			LOGGER.info(String.format("Failed to load configuration \"%s\".", data.getName()));
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
