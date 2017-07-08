package de.urkallinger.restclient.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement(name = "rest-data")
public class RestData extends RestDataBase {

	private StringProperty host;
	private StringProperty path;
	private StringProperty httpMethod;
	private StringProperty payload;
	private List<Header> headers;

	public RestData() {
		this.host = new SimpleStringProperty("");
		this.path = new SimpleStringProperty("");
		this.httpMethod = new SimpleStringProperty("");
		this.payload = new SimpleStringProperty("");
		this.headers = new ArrayList<>();
	}

	public StringProperty getHostProperty() {
		return host;
	}

	@XmlElement(name = "host")
	public String getHost() {
		return host.get();
	}

	public void setHost(String host) {
		this.host.set(host);
	}

	public StringProperty getPathProperty() {
		return path;
	}
	
	@XmlElement(name = "path")
	public String getPath() {
		return path.get();
	}

	public void setPath(String path) {
		this.path.set(path);
	}

	public StringProperty getHttpMethodProperty() {
		return httpMethod;
	}
	
	@XmlElement(name = "httpMethod")
	public String getHttpMethod() {
		return httpMethod.get();
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod.set(httpMethod);
	}

	public StringProperty getPayloadProperty() {
		return payload;
	}
	
	@XmlElement(name = "payload")
	public String getPayload() {
		return payload.get();
	}

	public void setPayload(String payload) {
		this.payload.set(payload);
	}
	
	@XmlElement(name = "headers")
	public List<Header> getHeaders() {
		return headers;
	}
	
	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}
	
	public void addHeader(Header header) {
		headers.add(header);
	}

	@Override
	public RestDataType getType() {
		return RestDataType.REST_DATA;
	}
}
