package de.urkallinger.restclient.controller;

import java.util.Collection;
import java.util.Optional;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.RestData;
import de.urkallinger.restclient.data.RestDataBase;
import de.urkallinger.restclient.data.RestDataContainer;
import de.urkallinger.restclient.data.RestDataType;
import de.urkallinger.restclient.data.SaveData;
import de.urkallinger.restclient.model.RestDataEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RestDataDialogController {
	@FXML
	TreeTableView<RestDataEntry> treeTable = new TreeTableView<>();
	@FXML
	TreeTableColumn<RestDataEntry, String> colName = new TreeTableColumn<>();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();
	
	private boolean ok = false;
	private RestDataType allowedType = RestDataType.REST_DATA;
	
	@FXML
	private void initialize() {
		 btnOk.setDisable(true);
		
		colName.setCellValueFactory(cellData -> cellData.getValue().getValue().getNameProperty());
		treeTable.setContextMenu(createContextMenu());
		
		// Muss mit Platform.runLater() ausgef�hrt werden, sonst gehts nicht.
		Platform.runLater(() -> setFocusOnTable());
		
		treeTable.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch(event.getCode()) {
			case DELETE:
				RestDataBase entry = getSelectedEntry();
				if(entry != null) {
					SaveData data = DataManager.loadData();
					// TODO: Funktioniert nicht mehr, da nicht rekursiv
					data.removeRestData(entry.getId());
					DataManager.saveData(data);
					
					 TreeItem<RestDataEntry> treeItem = treeTable.getSelectionModel().getSelectedItem();
					 treeItem.getParent().getChildren().remove(treeItem);
				}
				break;
			default:
				break;
			}
		});
	}
	
	private ContextMenu createContextMenu() {
		MenuItem item = new MenuItem("create new container");
		item.setOnAction(event -> {
			
			TextInputDialog nameChooser = new TextInputDialog();
			nameChooser.setTitle("Containername");
			nameChooser.setHeaderText("Choose a name for the new container.");
			
			Optional<String> result = nameChooser.showAndWait();
			
			RestDataEntry entry;
			RestDataContainer container = new RestDataContainer();
			if (result.isPresent()){
				container.setName(result.get());
				entry = new RestDataEntry(result.get(), container);
			} else {
				return;
			}
			
			TreeItem<RestDataEntry> treeItem = new TreeItem<>(entry);
			TreeItem<RestDataEntry> selection = treeTable.getSelectionModel().getSelectedItem();
			
			SaveData saveData = DataManager.loadData();
			if(selection != null) {
				saveData.addRestData(container, selection.getValue().getRestData().getId());
				selection.getChildren().add(treeItem);
			} else {
				// keine elemente im baum
				saveData.addRestData(container);
				treeTable.getRoot().getChildren().add(treeItem);
			}
			DataManager.saveData(saveData);
		});
		
		return new ContextMenu(item);
		
	}
	
	public RestDataBase getSelectedEntry() {
		TreeItem<RestDataEntry> item = treeTable.getSelectionModel().getSelectedItem();
		if (item != null && item.getValue().getRestData().getType() == allowedType) {
			return treeTable.getSelectionModel().getSelectedItem().getValue().getRestData();
		} else {
			return null;
		}
	}
	
	public boolean isOk() {
		return ok;
	}
	
	public void setAllowedType(RestDataType type) {
		allowedType = type;
	}
	
	private void buildTree(Collection<RestDataBase> data, TreeItem<RestDataEntry> parent) {
		// Containerknoten erstellen
		data.stream().filter(rd -> rd.getType() == RestDataType.CONTAINER).forEach(container -> {
			TreeItem<RestDataEntry> item = new TreeItem<>(new RestDataEntry(container.getName(), container));
			parent.getChildren().add(item);
			RestDataContainer c = (RestDataContainer) container;
			buildTree(c.getChildren(), item);
		});

		// Blattknoten erstellen
		data.stream().filter(rd -> rd.getType() == RestDataType.REST_DATA).forEach(rd -> {
			RestDataEntry entry = new RestDataEntry(rd.getName(), (RestData) rd);
			TreeItem<RestDataEntry> child = new TreeItem<>(entry);
			parent.getChildren().add(child);
		});
	}
	
	public void loadData(Collection<RestDataBase> content) {
		TreeItem<RestDataEntry> root = new TreeItem<>();
		buildTree(content, root);
		
		treeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			btnOk.setDisable(newValue.getValue().getRestData().getType() != allowedType);
		});
		
		treeTable.setShowRoot(false);
		treeTable.setRoot(root);
	}
	
	private void setFocusOnTable() {
		treeTable.getSelectionModel().select(0);
		treeTable.getFocusModel().focus(0);
		treeTable.requestFocus();
	}
	
	@FXML
	private void handleOk() {
		ok = true;
		Stage stage = (Stage) btnOk.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void handleCancel() {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}
}