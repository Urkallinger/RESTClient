package de.urkallinger.restclient.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class SaveData {
	
	private Map<UUID, RestData> restData;
	
	public SaveData() {
		this.restData = new HashMap<>();
	}
	
	public void addRestData(RestData data) {
		restData.put(data.getId(), data);
	}
	
	public Collection<RestData> getRestData() {
		return restData.values();
	}
	
	public void removeRestData(UUID id) {
		restData.remove(id);
	}
}
