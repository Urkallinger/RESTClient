package de.urkallinger.restclient.data;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlTransient
@XmlSeeAlso({RestData.class, RestDataContainer.class})
public abstract class RestDataBase {
	
	private UUID id;
	private StringProperty name;

	public RestDataBase() {
		this.id = UUID.randomUUID();
		this.name = new SimpleStringProperty("");
	}

	@XmlElement(name = "id")
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public StringProperty getNameProperty() {
		return name;
	}
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public abstract RestDataType getType();
	
	@Override
	public String toString() {
		return String.format("%s: %s", getType(), getName());
	}
}
