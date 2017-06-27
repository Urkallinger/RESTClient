package de.urkallinger.restclient.data;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class SaveData {
	
	private Map<String, RestData> restData;
	
	public SaveData() {
		this.restData = new HashMap<>();
	}

	public RestData getRestData(String name) {
		return restData.get(name);
	}
	
	public void addRestData(String name, RestData data) {
		restData.put(name, data);
	}
	
	public Map<String, RestData> getRestDataMap() {
		return restData;
	}
}
