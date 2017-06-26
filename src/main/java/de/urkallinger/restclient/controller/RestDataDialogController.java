package de.urkallinger.restclient.controller;

import de.urkallinger.restclient.model.RestDataEntry;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.text.Font;

public class RestDataDialogController {
	@FXML
	TreeTableView<RestDataEntry> treeTable = new TreeTableView<>();
	@FXML
	TreeTableColumn<RestDataEntry, String> colProject = new TreeTableColumn<>();
	@FXML
	TreeTableColumn<RestDataEntry, String> colName = new TreeTableColumn<>();
	
	@FXML
	private void initialize() {
		colProject.setCellValueFactory(cellData -> cellData.getValue().getValue().getProjectProperty());
		colName.setCellValueFactory(cellData -> cellData.getValue().getValue().getNameProperty());
		
		colProject.setCellFactory(call -> new TreeTableCell<RestDataEntry, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					setText(item);
					setFont(new Font(14));
				} else {
					setText(null);
				}
			}
		});
		
		colName.setCellFactory(call -> new TreeTableCell<RestDataEntry, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					setText(item);
					setFont(new Font(14));
				} else {
					setText(null);
				}
			}
		});
		
		TreeItem<RestDataEntry> root = new TreeItem<>();
		
		RestDataEntry rootData = new RestDataEntry("Local", "", null);
		RestDataEntry authData = new RestDataEntry("Local", "auth", null);
		
		TreeItem<RestDataEntry> prjItem = new TreeItem<>(rootData);
		TreeItem<RestDataEntry> authItem = new TreeItem<>(authData);
		
		root.getChildren().add(prjItem);
		prjItem.getChildren().add(authItem);
		
		treeTable.setShowRoot(false);
		treeTable.setRoot(root);
	}
}