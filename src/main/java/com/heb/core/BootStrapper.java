package com.heb.core;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.health.HealthCheckRegistry;

@Configuration
public class BootStrapper {

	private Logger LOGGER = LoggerFactory.getLogger(BootStrapper.class);
	private Application application;

	public BootStrapper(String propertiesFileName) {

		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-config.xml");
		Resource resource = context.getResource(propertiesFileName);
		Properties properties = null;

		try {
			File file = resource.getFile();

			if (file == null || !file.exists()) {
				LOGGER.error("File {} does not exist. Exiting.", propertiesFileName);
			}

			properties = loadProperties(file);

		} catch (Exception e) {
			LOGGER.error("System Error. Exiting.", e);
			System.exit(1);
		}
		application = new Application(properties, (MetricRegistry)context.getBean("metricRegistry"),  
				(HealthCheckRegistry)context.getBean("healthCheckRegistry"));
	}

	private Properties loadProperties(File propertiesFile) throws IOException {

		Properties prop = new Properties();

		try {
			StringReader reader = new StringReader(readFile(propertiesFile));
			prop.load(reader);

		} catch (IOException ex) {
			throw ex;
		}
		return prop;
	}

	private String readFile(File propertiesFile) throws IOException {

		return FileUtils.readFileToString(propertiesFile);
	}

	public static void main(String[] args) throws Exception {

		if (args.length == 0) {
			System.err.print("Usage : com.lab49.core.BootStrapper propertiesfile");
			System.exit(1);
		}
		String location = args[0];
		new BootStrapper(location);
	}
}
