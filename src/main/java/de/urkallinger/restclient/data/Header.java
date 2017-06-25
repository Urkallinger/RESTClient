package de.urkallinger.restclient.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement(name = "header")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Header {
	private StringProperty name;
	private StringProperty value;
	
	public Header() {
		this.name = new SimpleStringProperty("");
		this.value = new SimpleStringProperty("");
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
	
	public StringProperty getValueProperty() {
		return value;
	}
	
	public String getValue() {
		return value.get();
	}
	
	public void setValue(String value) {
		this.value.set(value);
	}
}
