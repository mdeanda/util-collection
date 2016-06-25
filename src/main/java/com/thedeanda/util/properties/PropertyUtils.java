package com.thedeanda.util.properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyUtils {
	private static final Logger log = LoggerFactory.getLogger(PropertyUtils.class);

	/**
	 * reads properties from local resource but overrides when others existing
	 * such that:
	 * <ul>
	 * <li>/etc/${name}.properties</li>
	 * <li>/etc/${name}/${name}.properties</li>
	 * <li>${user.home}/${name}.properties
	 * </ul>
	 * 
	 * @param name
	 *            name of properties file without path or extension: "example"
	 * @return
	 */
	public static Properties readProperties(String name) {
		Properties properties = new Properties();
		boolean loaded = false;
		String filename = name + ".properties";
		loaded = loadAndMerge(properties, PropertyUtils.class.getResourceAsStream("/" + filename));
		if (loaded) {
			log.debug("Properties file loaded: classpath:/" + filename);
		} else {
			log.warn("Properties file not loaded: classpath:/" + filename);
		}

		String[] paths = { "/etc/" + filename, "/etc/" + name + File.separator + filename,
				System.getProperty("user.home") + File.separator + filename, filename };

		for (String path : paths) {
			File overrides = new File(path);
			loadAndMerge(properties, overrides);
		}

		log.info("Loaded properties: {}", properties);
		return properties;
	}

	private static void loadAndMerge(Properties properties, File file) {
		if (file.exists()) {
			log.debug("Loading overrides file: {}", file);
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				loadAndMerge(properties, fis);
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						log.warn(e.getMessage(), e);
					}
				}
			}
		} else {
			log.debug("File not found: {}, skipping", file);
		}

	}

	private static boolean loadAndMerge(Properties props, InputStream is) {
		boolean loaded = false;
		Properties newProps = new Properties();
		if (is != null) {
			try {
				newProps.load(is);
				loaded = true;
			} catch (IOException e) {
				log.warn(e.getMessage(), e);
			}
		}

		if (loaded) {
			// merge
			Set<Object> keys = newProps.keySet();
			for (Object k : keys) {
				String key = (String) k;
				String value = newProps.getProperty(key);
				props.put(key, value);
			}

		}
		return loaded;

	}
}
