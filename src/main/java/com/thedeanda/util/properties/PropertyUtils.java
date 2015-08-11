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
	private static final Logger log = LoggerFactory
			.getLogger(PropertyUtils.class);

	/**
	 * reads properties from local resource but overrides if /etc/{filename}
	 * exits.
	 * 
	 * @param filename
	 *            without path separators before or embedded: foo.properties
	 * @return
	 */
	public static Properties readProperties(String filename) {
		Properties defaults = new Properties();
		boolean loaded = false;
		loaded = loadAndMerge(defaults,
				PropertyUtils.class.getResourceAsStream("/" + filename));
		if (loaded) {
			log.debug("properties file loaded: classpath:/" + filename);
		} else {
			log.warn("properties file not loaded: classpath:/" + filename);
		}

		String[] paths = { "/etc/" + filename,
				System.getProperty("user.home") + File.separator + filename,
				filename };

		for (String path : paths) {
			File overrides = new File(path);
			if (overrides.exists()) {
				log.debug("loading overrides file: {}", overrides);
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(overrides);
					loadAndMerge(defaults, fis);
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
				log.warn("file not found: {}, skipping", overrides);
			}
		}

		return defaults;
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
