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
	Button btnAdd = new Button();
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
		
		btnAdd.setGraphic(new ImageView(add));
		btnAdd.setOnAction(addPropertyHandler);
		
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
		
		colIcon.prefWidthProperty().bind(table.widthProperty().multiply(0.07));
		colName.prefWidthProperty().bind(table.widthProperty().multiply(0.46));
		colValue.prefWidthProperty().bind(table.widthProperty().multiply(0.46));

		addTableData();
		
		Platform.runLater(() -> setFocusOnTable());
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

		case ENTER:
			item = Optional.ofNullable(table.getSelectionModel().getSelectedItem());
			item.ifPresent(property -> editItem(property));
			break;
			
		case ESCAPE:
			if(editing) {
				resetEditArea();
				event.consume();
			}
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
		
		editing = true;
		txtName.setText(item.getName());
		txtValue.setText(item.getValue());
		btnAdd.setGraphic(new ImageView(save));
		btnAdd.setOnAction(getSavePropertyEvent(item));
	}
	
	private void setFocusOnTable() {
		table.getSelectionModel().select(0);
		table.getFocusModel().focus(0);
		table.requestFocus();
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
        			LOGGER.info(String.format("new property added (%s: %s)", prop.getName(), prop.getValue()));
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
            	
            	if(!txtName.getText().equals(old.getName())) {
            		saveData.getProperties().remove(old.getName());
            	}
            	
            	old.setName(txtName.getText());
            	old.setValue(txtValue.getText());
            	saveData.getProperties().put(old.getName(), old);
            	
            	DataManager.saveData(saveData);
            	resetEditArea();
            }
        };
	}
	
	private void resetEditArea() {
		editing = false;
		txtName.clear();
    	txtValue.clear();
    	btnAdd.setGraphic(new ImageView(add));
        btnAdd.setOnAction(addPropertyHandler);
	}
}
