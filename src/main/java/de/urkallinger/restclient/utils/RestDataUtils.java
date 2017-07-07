package de.urkallinger.restclient.utils;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import de.urkallinger.restclient.data.RestDataBase;
import de.urkallinger.restclient.data.RestDataContainer;
import de.urkallinger.restclient.data.RestDataType;

public class RestDataUtils {
	
	public static String tree(Collection<RestDataBase> data, int level) {
		StringBuilder builder = new StringBuilder();
		for (RestDataBase base : data) {
			if(level > 0) {
				builder.append(StringUtils.repeat("|   ", level-1));
				builder.append("|---");
				builder.append(base.getName());
				builder.append("\n");
			} else {
				builder.append(base.getName());
				builder.append("\n");
			}
			

			if (base.getType() == RestDataType.CONTAINER) {
				RestDataContainer c = (RestDataContainer) base;
				builder.append(tree(c.getChildren(), level + 1));
			}
		}

		return builder.toString();
	}
}
