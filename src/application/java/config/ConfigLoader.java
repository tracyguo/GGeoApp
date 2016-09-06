/**
* This program loads the config file
*
* @author  Tracy Guo
* @since   9/4/2016
*/
package application.java.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

public class ConfigLoader {
	private final Properties configProp = new Properties();

	private ConfigLoader() {
		InputStream inputStream = null;
		try {
			// Load the properties from config.properties file
			String propFileName = "config.properties";
			inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
			System.out.println("*****Reading properties from file*****");

			if (inputStream != null) {
				configProp.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static class LazyHolder {
		private static final ConfigLoader INSTANCE = new ConfigLoader();
	}

	public static ConfigLoader getInstance() {
		return LazyHolder.INSTANCE;
	}

	public String getProperty(String key) {
		return configProp.getProperty(key);
	}

	public Set<String> getAllPropertyNames() {
		return configProp.stringPropertyNames();
	}

	public boolean containsKey(String key) {
		return configProp.containsKey(key);
	}
}
