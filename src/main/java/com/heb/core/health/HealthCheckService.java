package com.heb.core.health;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.yammer.metrics.health.HealthCheck;
import com.yammer.metrics.health.HealthCheckRegistry;

@ManagedResource
public class HealthCheckService {

	@Autowired
	private HealthCheckRegistry healthChecks;

	@Autowired
	private Map<String, HealthCheck> healthCheckMap;

	public HealthCheckService(){
		
	}
}
