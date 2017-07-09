package de.urkallinger.restclient.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement(name = "property")
public class Property {
	StringProperty name;
	StringProperty value;
	
	public Property() {
		name = new SimpleStringProperty("");
		value = new SimpleStringProperty("");
	}

	public final StringProperty nameProperty() {
		return this.name;
	}

	@XmlElement(name = "name")
	public final String getName() {
		return this.nameProperty().get();
	}

	public final void setName(final String name) {
		this.nameProperty().set(name);
	}

	public final StringProperty valueProperty() {
		return this.value;
	}

	@XmlElement(name = "value")
	public final String getValue() {
		return this.valueProperty().get();
	}

	public final void setValue(final String value) {
		this.valueProperty().set(value);
	}
}
