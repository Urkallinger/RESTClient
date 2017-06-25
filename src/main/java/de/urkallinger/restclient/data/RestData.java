package de.urkallinger.restclient.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement(name = "rest")
@XmlAccessorType(XmlAccessType.PROPERTY)

public class RestData {

	@XmlTransient
	private final static Logger LOGGER = LoggerFactory.getLogger(RestData.class);
	
	private StringProperty name;
	private StringProperty host;
	private StringProperty path;
	private StringProperty httpMethod;
	private StringProperty payload;
	private List<Header> headers;

	public RestData() {
		this.name = new SimpleStringProperty("");
		this.host = new SimpleStringProperty("");
		this.path = new SimpleStringProperty("");
		this.httpMethod = new SimpleStringProperty("");
		this.payload = new SimpleStringProperty("");
		this.headers = new ArrayList<>();
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

	public StringProperty getHostProperty() {
		return host;
	}
	
	public String getHost() {
		return host.get();
	}

	public void setHost(String host) {
		this.host.set(host);
	}

	public StringProperty getPathProperty() {
		return path;
	}
	
	public String getPath() {
		return path.get();
	}

	public void setPath(String path) {
		this.path.set(path);
	}

	public StringProperty getHttpMethodProperty() {
		return httpMethod;
	}
	
	public String getHttpMethod() {
		return httpMethod.get();
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod.set(httpMethod);
	}

	public StringProperty getPayloadProperty() {
		return payload;
	}
	
	public String getPayload() {
		return payload.get();
	}

	public void setPayload(String payload) {
		this.payload.set(payload);
	}
	
	public List<Header> getHeaders() {
		return headers;
	}
	
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}
	
	public void addHeader(Header header) {
		headers.add(header);
	}
}
