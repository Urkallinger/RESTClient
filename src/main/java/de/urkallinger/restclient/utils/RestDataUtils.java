package de.urkallinger.restclient.utils;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.urkallinger.restclient.data.Property;
import de.urkallinger.restclient.data.RestDataBase;
import de.urkallinger.restclient.data.RestDataContainer;
import de.urkallinger.restclient.data.RestDataType;

public class RestDataUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RestDataUtils.class);
	private static final Pattern PROPERTY_PATTERN = Pattern.compile("\\{([^\\}]*)\\}");
	
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
	
	public static String replaceProperties(String text, Map<String, Property> properties) {
		Matcher m = PROPERTY_PATTERN.matcher(text);

		try {
			while(m.find()) {
				String name = m.group(1);
				String variable = String.format("{%s}", name);
				Property property = properties.get(name);
				
				if(property != null) {
					text = text.replace(variable, property.getValue());
				} else {
					throw new Exception(String.format("No property \"%s\" found.", name));
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
		return text;
	}
}
