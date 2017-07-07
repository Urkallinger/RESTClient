package de.urkallinger.restclient.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement(name = "configuration")
@XmlAccessorType(XmlAccessType.FIELD)
public class SaveData {

	@XmlTransient
	private static final Logger LOGGER = LoggerFactory.getLogger(SaveData.class);
	
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
				String pn = map.get(parent).getName();
				LOGGER.error("Can not add %s to %s because %s is not a container.", data.getName(), pn, pn);
			}
		} else {
			map.values().stream()
				.filter(rd -> rd.getType() == RestDataType.CONTAINER)
				.map(rd -> (RestDataContainer) rd)
				.forEach(container -> addRecursiv(container.getChildrenMap(), parent, data));
		}
	}

	public Collection<RestDataBase> getRestData() {
		return restData.values() != null ? restData.values() : new ArrayList<>();
	}
	
	public Map<UUID, RestDataBase> getRestDataMap() {
		return restData;
	}

	public void removeRestData(UUID id) {
		removeRecursiv(restData, id);
	}
	
	private void removeRecursiv(Map<UUID, RestDataBase> map, UUID id) {
		if(map.containsKey(id)) {
			map.remove(id);
		} else {
			map.values().stream()
				.filter(rd -> rd.getType() == RestDataType.CONTAINER)
				.map(rd -> (RestDataContainer) rd)
				.forEach(container -> removeRecursiv(container.getChildrenMap(), id));
		}
	}
	
	public static boolean updateRestData(Map<UUID, RestDataBase> map, RestDataBase data) {
		if(map.containsKey(data.getId())) {
			map.put(data.getId(), data);
			return true;
		} else {
			List<RestDataContainer> containers = map.values().stream()
					.filter(rd -> rd.getType() == RestDataType.CONTAINER)
					.map(rd -> (RestDataContainer) rd)
					.collect(Collectors.toList());
			for(RestDataContainer c : containers) {
				if(updateRestData(c.getChildrenMap(), data)) {
					return true;
				}
			}
			
			return false;
		}
	}
}
