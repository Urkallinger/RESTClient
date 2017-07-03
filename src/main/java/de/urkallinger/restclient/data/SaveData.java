package de.urkallinger.restclient.data;

import java.util.ArrayList;
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

	private Map<UUID, RestDataBase> restData;

	public SaveData() {
		this.restData = new HashMap<>();
	}

	public void addRestData(RestDataBase data) {
		addRestData(data, null);
	}

	public void addRestData(RestDataBase data, UUID parent) {
		if (parent != null) {
			addRecursiv(restData, parent, data);
		} else {
			restData.put(data.getId(), data);
		}
	}
	
	private void addRecursiv(Map<UUID, RestDataBase> map, UUID parent, RestDataBase data) {
		if(map.containsKey(parent)) {
			if(map.get(parent).getType() == RestDataType.CONTAINER) {
				((RestDataContainer) map.get(parent)).addChild(data);
			} else {
				map.values().stream()
					.filter(rd -> rd.getType() == RestDataType.CONTAINER)
					.map(rd -> (RestDataContainer) rd)
					.forEach(container -> addRecursiv(container.getChildrenMap(), parent, data));
			}
		}
	}

	public Collection<RestDataBase> getRestData() {
		return restData.values() != null ? restData.values() : new ArrayList<>();
	}

	public void removeRestData(UUID id) {
		restData.remove(id);
	}
}
