package com.heb.core.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.heb.core.Application;
import com.heb.core.annotation.Audit;
import com.heb.core.annotation.LogRequest;
import com.heb.core.annotation.LogResponse;
import com.yammer.metrics.Meter;
import com.yammer.metrics.MetricRegistry;
import com.yammer.metrics.Timer;
import com.yammer.metrics.Timer.Context;
import com.yammer.metrics.annotation.Metered;
import com.yammer.metrics.annotation.Timed;

/**
 * Proxy Class which wraps multiple services about a target class. The target class must be
 * annotated to perform the desired operations.
 * 
 * The following annotations are available.
 * 
 * <code>@Raw</code> -
 * <code>@Audit</code> -
 * <code>@Timed</code> -
 * <code>@Metered</code> -
 * <code>@LogRequest</code> -
 * <code>@LogResponse</code> -
 * 
 */
public class CoreServiceInvocationHandler implements InvocationHandler {

	private Logger LOGGER = LoggerFactory.getLogger(CoreServiceInvocationHandler.class);

	private static final String METER_POST_FIX = "_meter";
	private static final String TIMED_POST_FIX = "_timer";

	private static final String DEBUG = "debug";
	private static final String TRACE = "trace";
	private static final String INFO = "info";
	
	private static final String AUDIT = "Audit-";
	private static final String REQUEST = "Request-";
	private static final String RESULT = "Result-";
	
	private final Object target;
	private final Application application;

	private final Map<String, Object> metricsMap = new HashMap<String, Object>();

	public CoreServiceInvocationHandler(Application application, Object target) {
		this.application = application;
		this.target = target;

		MetricRegistry metricRegistry = application.getMetricRegistry();

		Method[] methods = target.getClass().getMethods();

		//Create the metrics based on the names in the Annotations. 
		//If there is none, use the method name
		for (Method method : methods) {

			String methodName = method.getName();
			Annotation[] annotations = method.getAnnotations();

			for (Annotation annotation : annotations) {

				if (annotation.annotationType().getSimpleName().equals(Timed.class.getSimpleName())) {

					String metricKey = createTimedMetricKey(((Timed) annotation), methodName);

					LOGGER.debug("Created Key :" + metricKey);
					this.metricsMap.put(metricKey, metricRegistry.timer(metricKey));
				}
				if (annotation.annotationType().getSimpleName().equals(Metered.class.getSimpleName())) {

					String metricKey = createMeterMetricKey(((Metered) annotation), methodName);
					LOGGER.debug("Created Key :" + metricKey);
					this.metricsMap.put(metricKey, metricRegistry.meter(metricKey));
				}
			}
		}
	}

	private String createMeterMetricKey(Metered annotation, String methodName) {

		String name = null;
		if (annotation != null)
			name = annotation.name();

		return name == null || name.isEmpty() ? methodName + METER_POST_FIX : name + METER_POST_FIX;
	}

	private String createTimedMetricKey(Timed annotation, String methodName) {

		String name = null;
		if (annotation != null)
			name = annotation.name();

		return name == null || name.isEmpty() ? methodName + TIMED_POST_FIX : name + TIMED_POST_FIX;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		if (isRaw(method)) {
			return method.invoke(target, args);
		}
		
		method = this.target.getClass().getMethod(method.getName(), method.getParameterTypes());

		logRequest(method, args);
		audit(method, args);

		String timeMetricKey = this.createTimedMetricKey(method.getAnnotation(Timed.class), method.getName());
		String meterMetricKey = this.createMeterMetricKey(method.getAnnotation(Metered.class), method.getName());

		Timer timer = (Timer) this.metricsMap.get(timeMetricKey);
		Meter meter = (Meter) this.metricsMap.get(meterMetricKey);

		if (meter != null) {
			meter.mark();
		}

		Context context = null;
		if (timer != null) {
			context = timer.time();
		}

		Object returned;
		try {

			returned = method.invoke(target, args);
		} finally {
			if (context != null)
				context.stop();
		}

		LogResponse(method, returned);
		audit(method, returned);

		return returned;
	}

	private void LogResponse(Method method, Object returned) {
		if (method.getAnnotation(com.heb.core.annotation.LogResponse.class)  != null){			
			LogResponse LogResponse = method.getAnnotation(com.heb.core.annotation.LogResponse.class);			
			logObject(LogResponse.name(), returned, RESULT);			
		}		
	}

	private void audit(Method method, Object returned) {
		if (method.getAnnotation(com.heb.core.annotation.Audit.class)  != null){
			Audit audit = method.getAnnotation(com.heb.core.annotation.Audit.class);			
			logObject(audit.name(), returned, AUDIT + RESULT);						
		}		
	}

	private boolean isRaw(Method method) {
		
		return method.getAnnotation(com.heb.core.annotation.Raw.class)!=null ? true : false;
	}

	private void audit(Method method, Object[] args) {
		if (method.getAnnotation(com.heb.core.annotation.Audit.class)  != null){
			Audit audit = method.getAnnotation(com.heb.core.annotation.Audit.class);			
			logObject(audit.name(), convertArgsToString(args), AUDIT + REQUEST);									
		}
	}

	private void logRequest(Method method, Object[] args) {
		if (method.getAnnotation(com.heb.core.annotation.LogRequest.class) != null){
			LogRequest logRequest = method.getAnnotation(com.heb.core.annotation.LogRequest.class);			
			logObject(logRequest.name(), convertArgsToString(args), REQUEST);												
		}
	}
	
	private void logObject(String name, Object object, String prefix) {

		if (object==null)
			object = "NULL";
		
		if (name == null || name.isEmpty()){
			LOGGER.debug(prefix + " " + object.toString());
			return;
		}
		
		if (name.equalsIgnoreCase(TRACE) && LOGGER.isTraceEnabled()){
			LOGGER.debug(prefix + " " + object.toString());
		}
		if (name.equalsIgnoreCase(DEBUG) && LOGGER.isDebugEnabled()){
			LOGGER.debug(prefix + " " + object.toString());
		}
		if (name.equalsIgnoreCase(INFO) && LOGGER.isInfoEnabled()){
			LOGGER.debug(prefix + " " + object.toString());
		}
	}

	private Object convertArgsToString(Object[] args) {
		
		StringBuffer buffer = new StringBuffer();
		
		for (Object object : args){
			buffer.append(object.toString() + " ");
		}
		return buffer.toString();
	}


	
	public Application getApplication() {
		return application;
	}
}
