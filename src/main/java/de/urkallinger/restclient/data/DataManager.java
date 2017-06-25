package de.urkallinger.restclient.data;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataManager {

	private final static Logger LOGGER = LoggerFactory.getLogger(DataManager.class);
	
	private static final String CONFIG_XML = "data.xml";
	
	private DataManager() {
	}
	
	public static boolean configurationExists() {
		return new File(CONFIG_XML).exists();
	}
	
	public static SaveData loadData() {
		File cfgFile = new File(CONFIG_XML);
		SaveData config = null;
		if (cfgFile.exists()) {
			try {
				JAXBContext context = JAXBContext.newInstance(SaveData.class);
				Unmarshaller m = context.createUnmarshaller();
				config = (SaveData) m.unmarshal(cfgFile);
			} catch (JAXBException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		if(config == null) config = new SaveData();
		return config;
	}
	
	public static void saveData(SaveData cfg) {
		File cfgFile = new File(CONFIG_XML);
		try {
			if(!cfgFile.exists()) cfgFile.createNewFile();
			
			JAXBContext context = JAXBContext.newInstance(SaveData.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(cfg, cfgFile);
		} catch (JAXBException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public static void createNewConfiguration() {
		saveData(new SaveData());
	}
	
	public static void createOrUpdateConfiguration() {
		if(!configurationExists()) {
			createNewConfiguration();
		} else {
			SaveData cfg = loadData();
			saveData(cfg);
		}
	}
}
