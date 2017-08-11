package de.urkallinger.restclient.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.Property;
import de.urkallinger.restclient.data.SaveData;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PropertiesDialogController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesDialogController.class);
	
	@FXML
	TableView<Property> table = new TableView<>();
	@FXML
	TableColumn<Property, Boolean> colIcon = new TableColumn<>();
	@FXML
	TableColumn<Property, String> colName = new TableColumn<>();
	@FXML
	TableColumn<Property, String> colValue = new TableColumn<>();
	@FXML
	TextField txtName = new TextField();
	@FXML
	TextField txtValue = new TextField();
	@FXML
	Button btnCommit = new Button();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();
	
	private Optional<Property> selection = Optional.empty();
	private final Image add = new Image(getClass().getResourceAsStream("/images/add.png")); 
	private final Image save = new Image(getClass().getResourceAsStream("/images/save.png"));
	
	private EventHandler<ActionEvent> addPropertyHandler;
	private boolean editing = false;
	
	@FXML
	private void initialize() {
		
		final Image editable = new Image(getClass().getResourceAsStream("/images/editable.png"));
		final Image lock = new Image(getClass().getResourceAsStream("/images/lock.png"));
		
		addPropertyHandler = getAddPropertyEvent();
		
		btnCommit.setGraphic(new ImageView(add));
		btnCommit.setOnAction(addPropertyHandler);
		
		colIcon.setCellValueFactory(cellData -> cellData.getValue().sysConstProperty());
		colName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		colValue.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

		colIcon.setCellFactory(col -> {
			return new TableCell<Property, Boolean>() {
				protected void updateItem(Boolean item, boolean empty) {
					if (item != null && !empty) {
						setGraphic(item ? new ImageView(lock) : new ImageView(editable));
					}
				};
			};
		});
		
		colIcon.setPrefWidth(35.0);
		colName.prefWidthProperty().bind(table.widthProperty().multiply(0.46));
		colValue.prefWidthProperty().bind(table.widthProperty().multiply(0.46));

		addTableData();
		setFocusOnTable();
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
	public void handleEditKeyInput(KeyEvent event) {
		switch (event.getCode()) {
		case ENTER:
			btnCommit.fire();
			break;

		default:
			break;
		}
	}
	
	@FXML
	public void handleTableKeyEvent(KeyEvent event) {
		Optional<Property> item;
		switch (event.getCode()) {
		case DELETE:
			item = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
			item.ifPresent(property -> {
				if(property.isSysConst()) return;
				
				table.getItems().remove(property);
				table.refresh(); // muss aktualisert werden, da sonst das icon nicht verschwindet
				
				SaveData saveData = DataManager.loadData();
				saveData.getProperties().remove(property.getName());
				DataManager.saveData(saveData);
				
				LOGGER.info(String.format("Property deleted (%s: %s)", property.getName(), property.getValue()));
			});
			break;

		case ENTER:
			item = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
			item.ifPresent(property -> editItem(property));
			event.consume();
			break;
						
		default:
			break;
		}
	}
	
	@FXML
	public void handleTableMouseClick(MouseEvent event) {
		Optional<Property> item = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
		if(event.getClickCount() == 2 && item.isPresent()) {
			editItem(item.get());
		}
	}
	
	public Optional<Property> getSelection() {
		return selection;
	}
	
	public boolean isEditing() {
		return editing;
	}
	
	private void editItem(Property item) {
		if(item.isSysConst()) return;
		
		Platform.runLater(() -> txtName.requestFocus());
		editing = true;
		txtName.setText(item.getName());
		txtValue.setText(item.getValue());
		btnCommit.setGraphic(new ImageView(save));
		btnCommit.setOnAction(getSavePropertyEvent(item));
	}
	
	private void setFocusOnTable() {
		Platform.runLater(() -> {
			table.getSelectionModel().select(0);
			table.getFocusModel().focus(0);
			table.requestFocus();
		});
	}
	
	private void addTableData() {
		Map<String, Property> properties = DataManager.loadData().getProperties();
		// Add System Consts
		properties.values().stream()
		.sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
		.filter(p -> p.isSysConst())
		.forEach(p -> table.getItems().add(p));
		// Add User Properties
		properties.values().stream()
		.sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
		.filter(p -> !p.isSysConst())
		.forEach(p -> table.getItems().add(p));
	}
	
	private EventHandler<ActionEvent> getAddPropertyEvent() {
		return new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
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
        			resetEditArea();
        			LOGGER.info(String.format("Property added (%s: %s)", prop.getName(), prop.getValue()));
        		} else {
        			String msg = String.format("Property \"%s\" already exists. "
        					+ "Edit the value of the existing property in the table "
        					+ "or choose another name.",  prop.getName());
        			LOGGER.error(msg);
        		}
            }
        };
	}
	
	private EventHandler<ActionEvent> getSavePropertyEvent(Property old) {
		return new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	SaveData saveData = DataManager.loadData();
            	
            	// Eindeutiger Name wurde geändert -> Altes Property löschen und neues erstellen
            	if(!txtName.getText().equals(old.getName())) {
            		saveData.getProperties().remove(old.getName());
            	}
            	
            	String oldProp = String.format("(%s: %s)", old.getName(), old.getValue());
            	
            	// Altes Property wiederverwenden, da dieses schon ein binding mit der GUI hat.
            	old.setName(txtName.getText());
            	old.setValue(txtValue.getText());
            	saveData.getProperties().put(old.getName(), old);
            	
            	String newProp = String.format("(%s: %s)", old.getName(), old.getValue());
            	
            	DataManager.saveData(saveData);
            	LOGGER.info(String.format("Property modified (%s -> %s)", oldProp, newProp));
            	resetEditArea();
            }
        };
	}
	
	public void resetEditArea() {
		editing = false;
		txtName.clear();
    	txtValue.clear();
    	btnCommit.setGraphic(new ImageView(add));
        btnCommit.setOnAction(addPropertyHandler);
        setFocusOnTable();
	}
}
