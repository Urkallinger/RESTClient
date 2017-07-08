package de.urkallinger.restclient.data;

import java.util.List;
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
	
	private RestDataContainer restData;

	public SaveData() {
		this.restData = new RestDataContainer();
		this.restData.setName("root");
	}

	public RestDataContainer getRestData() {
		return restData;
	}
	
	public void addRestData(RestDataBase data) {
		restData.getChildrenMap().put(data.getId(), data);
	}

	public void addRestData(RestDataBase rd, UUID parentId) {
		walkTree(restData, container -> {
			if(container.getId().equals(parentId)) {
				container.getChildrenMap().put(rd.getId(), rd);
				return true;
			}
			return false;
		});
	}

	public boolean removeRestData(UUID id) {
		return walkTree(restData, container ->  {
			if(container.getChildrenMap().containsKey(id)) {
				container.getChildrenMap().remove(id);
				return true;
			}
			return false;
		});
	}
	
	public boolean updateRestData(RestDataBase rd) {
		return walkTree(restData, container ->  {
			if(container.getChildrenMap().containsKey(rd.getId())) {
				container.getChildrenMap().put(rd.getId(), rd);
				return true;
			}
			return false;
		});
	}
	
	public static boolean walkTree(RestDataContainer parent, RecursionCallback rc) {
		if(rc.handle(parent)) {
			return true;
		}
		
		List<RestDataContainer> containers = parent.getChildren().stream()
				.filter(child -> child.getType() == RestDataType.CONTAINER)
				.map(child -> (RestDataContainer) child)
				.collect(Collectors.toList());

		for (RestDataContainer c : containers) {
			if (walkTree(c, rc)) {
				return true;
			}
		}
		
		return false;
	}
}
