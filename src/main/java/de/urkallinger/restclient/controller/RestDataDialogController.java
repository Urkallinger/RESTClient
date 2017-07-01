package de.urkallinger.restclient.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.urkallinger.restclient.data.DataManager;
import de.urkallinger.restclient.data.RestData;
import de.urkallinger.restclient.data.SaveData;
import de.urkallinger.restclient.model.RestDataEntry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RestDataDialogController {
	@FXML
	TreeTableView<RestDataEntry> treeTable = new TreeTableView<>();
	@FXML
	TreeTableColumn<RestDataEntry, String> colProject = new TreeTableColumn<>();
	@FXML
	TreeTableColumn<RestDataEntry, String> colName = new TreeTableColumn<>();
	@FXML
	Button btnOk = new Button();
	@FXML
	Button btnCancel = new Button();
	
	private boolean ok = false;
	
	@FXML
	private void initialize() {
		 btnOk.setDisable(true);
		
		colProject.setCellValueFactory(cellData -> cellData.getValue().getValue().getProjectProperty());
		colName.setCellValueFactory(cellData -> cellData.getValue().getValue().getNameProperty());
		
		loadData();
		
		// Muss mit Platform.runLater() ausgeführt werden, sonst gehts nicht.
		Platform.runLater(() -> setFocusOnTable());
		
		treeTable.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch(event.getCode()) {
			case DELETE:
				RestData entry = getSelectedEntry();
				if(entry != null) {
					SaveData data = DataManager.loadData();
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
	
	public RestData getSelectedEntry() {
		if (treeTable.getSelectionModel().getSelectedItem() != null) {
			return treeTable.getSelectionModel().getSelectedItem().getValue().getRestData();
		} else {
			return null;
		}
	}
	
	public boolean isOk() {
		return ok;
	}
	
	private void loadData() {
		Collection<RestData> restData = DataManager.loadData().getRestData();
		List<TreeItem<RestDataEntry>> entries = new ArrayList<>();
		
		Set<String> projects = DataManager.loadData().getRestData().stream()
				.map(rs -> rs.getProject())
				.collect(Collectors.toSet());
		
		projects.forEach(prj -> {
			TreeItem<RestDataEntry> item = new TreeItem<>(new RestDataEntry(prj, "", null));
			entries.add(item);
			
			restData.stream()
				.filter(rs -> rs.getProject().equals(prj))
				.forEach(rs -> {
					RestDataEntry entry = new RestDataEntry(rs.getProject(), rs.getName(), rs);
					TreeItem<RestDataEntry> child = new TreeItem<>(entry);
					item.getChildren().add(child);
				});
		});
		
		TreeItem<RestDataEntry> root = new TreeItem<>();
		root.getChildren().addAll(entries);

		treeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			btnOk.setDisable(newValue == null || newValue.getValue().getRestData() == null);
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