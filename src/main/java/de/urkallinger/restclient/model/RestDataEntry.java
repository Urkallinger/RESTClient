package de.urkallinger.restclient.model;

import de.urkallinger.restclient.data.RestData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RestDataEntry {

	private StringProperty name;
	private RestData restData;
	
	public RestDataEntry() {
		this.name = new SimpleStringProperty("");
		this.restData = null;
	}
	
	public RestDataEntry(String name, RestData restData) {
		this.name = new SimpleStringProperty(name);
		this.restData = restData;
	}
	
	public StringProperty getNameProperty() {
		return name;
	}
	
	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public RestData getRestData() {
		return restData;
	}
	
	public void setRestData(RestData restData) {
		this.restData = restData;
	}
}
