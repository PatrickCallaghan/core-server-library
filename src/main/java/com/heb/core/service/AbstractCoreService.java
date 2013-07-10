package com.heb.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heb.core.Application;

public abstract class AbstractCoreService {

	public final static String DEFAULT_SEPARATOR = ".";

	private Logger LOGGER = LoggerFactory.getLogger(AbstractCoreService.class);
	
	private final Application application;
	private final String service;

	public AbstractCoreService(Application application, String service) {
		this.service = service;
		this.application = application;
	}
}

