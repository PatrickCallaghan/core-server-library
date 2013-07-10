package com.heb.core;

import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.yammer.metrics.Counter;
import com.yammer.metrics.Meter;
import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.Timer;
import com.yammer.metrics.health.HealthCheck;
import com.yammer.metrics.health.HealthCheckRegistry;

public class Application implements ApplicationContextAware{

	private final Properties properties;
	private final MetricRegistry metricRegistry;
	private final HealthCheckRegistry healthCheckRegistry;
	
	private ApplicationContext springApplicationContext;
	private Map<String, HealthCheck> healthChecks;
	
	public Application(Properties properties, MetricRegistry metricRegistry, HealthCheckRegistry healthCheckRegistry) {		
		this.properties = properties;
		this.healthCheckRegistry = healthCheckRegistry;
		this.metricRegistry = metricRegistry;
		
		//String appName = properties.getProperty("appName"); 			
	}
	
	@SuppressWarnings("unchecked")
	public void init(){
		this.healthChecks = (Map<String, HealthCheck>) springApplicationContext.getBean("healthCheckMap");
		
		for(String name : this.healthChecks.keySet()){
			this.healthCheckRegistry.register(name, this.healthChecks.get(name));
		}
		this.runHealthChecks();
	}
	
	public void cleanUp(){
		
	}

	public void addHealthCheck(String healthCheckName, HealthCheck healthCheck){
		this.healthCheckRegistry.register(healthCheckName, healthCheck);
	}
	
	public Counter addCounter(String counterName){
		return this.metricRegistry.counter(counterName);	
	}
	
	public Timer addTimer(String timerName){
		return this.metricRegistry.timer(timerName);
	}
	
	public Meter addMeter(String meterName){
		return this.metricRegistry.meter(meterName);
	}
	
	public MetricRegistry getMetricRegistry() {
		return metricRegistry;
	}

	public HealthCheckRegistry getHealthCheckRegistry() {
		return healthCheckRegistry;
	}

	public String getProperty(String name) {
		return this.properties.getProperty(name);
	}

	public String getProperty(String name, String defaultProperty) {
		return this.properties.getProperty(name) == null ? defaultProperty : this.properties.getProperty(name);
	}
	
	public Properties getProperties() {
		return properties;
	}

	public void runHealthChecks(){
		this.healthCheckRegistry.runHealthChecks();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springApplicationContext = applicationContext;	
	}
}
