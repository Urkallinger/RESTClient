package de.urkallinger.restclient.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rest-data-container")
public class RestDataContainer extends RestDataBase {

	private Map<UUID, RestDataBase> children;
	
	public RestDataContainer() {
		children = new HashMap<>();
	}

	public Collection<RestDataBase> getChildren() {
		return children.values() != null ? children.values() : new ArrayList<>();
	}
	
	@XmlElement(name = "children")
	public Map<UUID, RestDataBase> getChildrenMap() {
		return children;
	}
	
	public void setChildrenMap(Map<UUID, RestDataBase> children) {
		this.children = children;
	}
	
	public void addChild(RestDataBase child) {
		children.put(child.getId(), child);
	}

	@Override
	public RestDataType getType() {
		return RestDataType.CONTAINER;
	}
}
