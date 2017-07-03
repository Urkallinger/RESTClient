package de.urkallinger.restclient.model;

import de.urkallinger.restclient.data.RestDataBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RestDataEntry {

	private StringProperty name;
	private RestDataBase restData;
	
	public RestDataEntry() {
		this.name = new SimpleStringProperty("");
		this.restData = null;
	}
	
	public RestDataEntry(String name, RestDataBase restData) {
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
	
	public RestDataBase getRestData() {
		return restData;
	}
	
	public void setRestData(RestDataBase restData) {
		this.restData = restData;
	}
}
