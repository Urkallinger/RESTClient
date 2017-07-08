package de.urkallinger.restclient.controller;

import java.util.Optional;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.RestDataBase;
import de.urkallinger.restclient.data.RestDataContainer;
import de.urkallinger.restclient.data.RestDataType;
import de.urkallinger.restclient.data.SaveData;
import de.urkallinger.restclient.dialogs.NameChooser;
import de.urkallinger.restclient.model.RestDataEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
		treeTable.setOnContextMenuRequested(event -> {
			TreeItem<RestDataEntry> item = treeTable.getSelectionModel().getSelectedItem();
			if(item != null && item.getValue().getRestData().getType() != RestDataType.CONTAINER) {
				treeTable.getContextMenu().hide();
			}
		});
		
		// Muss mit Platform.runLater() ausgeführt werden, sonst gehts nicht.
		Platform.runLater(() -> setFocusOnTable());
		
		treeTable.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch(event.getCode()) {
			case DELETE:
				TreeItem<RestDataEntry> treeItem = treeTable.getSelectionModel().getSelectedItem();
				if(treeItem != null && treeItem.getValue() != null) {
					RestDataBase entry = treeItem.getValue().getRestData();
					SaveData data = DataManager.loadData();
					data.removeRestData(entry.getId());
					DataManager.saveData(data);
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
		item.setOnAction(event -> createNewContainer());

		return new ContextMenu(item);
		
	}
	
	/**
	 * Kontextmenü Methode
	 */
	private void createNewContainer() {
		NameChooser nameChooser = new NameChooser("Containername", "Choose a name for the new container.");
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
		Image icon = new Image(getClass().getResourceAsStream("/images/container.png"));
		treeItem.setGraphic(new ImageView(icon));
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
	
	private void buildTree(RestDataContainer content, TreeItem<RestDataEntry> parent) {
		// Containerknoten erstellen
		content.getChildren().stream()
			.filter(rd -> rd.getType() == RestDataType.CONTAINER)
			.sorted((c1, c2) -> c1.getName().compareTo(c2.getName()))
			.forEach(container -> {
				TreeItem<RestDataEntry> item = createTreeItem(container);
				parent.getChildren().add(item);
				buildTree(((RestDataContainer) container), item);
			});

		// Blattknoten erstellen
		content.getChildren().stream()
			.filter(rd -> rd.getType() == RestDataType.REST_DATA)
			.sorted((rd1, rd2) -> rd1.getName().compareTo(rd2.getName()))
			.forEach(rd -> {
				TreeItem<RestDataEntry> child = createTreeItem(rd);
				parent.getChildren().add(child);
			});
	}
	
	public void loadData(RestDataContainer content) {
		TreeItem<RestDataEntry> root = new TreeItem<>(new RestDataEntry(content.getName(), content));
		buildTree(content, root);
		
		treeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			btnOk.setDisable(newValue == null || newValue.getValue().getRestData().getType() != allowedType);
		});
		
		treeTable.setShowRoot(false);
		treeTable.setRoot(root);
	}
	
	private void setFocusOnTable() {
		treeTable.getSelectionModel().select(0);
		treeTable.getFocusModel().focus(0);
		treeTable.requestFocus();
	}
	
	private TreeItem<RestDataEntry> createTreeItem(RestDataBase data) {
		TreeItem<RestDataEntry> item = new TreeItem<>(new RestDataEntry(data.getName(), data));
		String imgPath;
		if(data.getType() == RestDataType.CONTAINER) {
			imgPath = "/images/container.png";
		} else {
			imgPath = "/images/configuration.png";
		}
		Image icon = new Image(getClass().getResourceAsStream(imgPath));
		item.setGraphic(new ImageView(icon));
		return item;
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