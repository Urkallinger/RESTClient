package de.urkallinger.restclient.model;

import de.urkallinger.restclient.data.RestData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RestDataEntry {

	private StringProperty project;
	private StringProperty name;
	private RestData restData;
	
	public RestDataEntry() {
		this.project = new SimpleStringProperty("");
		this.name = new SimpleStringProperty("");
		this.restData = null;
	}
	
	public RestDataEntry(String project, String name, RestData restData) {
		this.project = new SimpleStringProperty(project);
		this.name = new SimpleStringProperty(name);
		this.restData = restData;
	}
	
	public StringProperty getProjectProperty() {
		return project;
	}
	
	public String getProject() {
		return project.get();
	}
	
	public void setProject(String project) {
		this.project.set(project);
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
