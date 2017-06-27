package de.urkallinger.restclient.model;

public class SaveDialogData {
	private String project;
	private String name;
	
	public SaveDialogData(String project, String name) {
		this.project = project;
		this.name = name;
	}
	
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
