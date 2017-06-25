package de.urkallinger.restclient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ResponseController {

	@FXML
	private TextArea taResponse = new TextArea();

	@FXML
	private void initialize() {
	}
	
	public void clearContent() {
		taResponse.clear();
	}
	
	public void setText(String text) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Object json = mapper.readValue(text, Object.class);
			taResponse.setText(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json));
		} catch (Exception e) {
			taResponse.setText(text);
		}
	}
}
