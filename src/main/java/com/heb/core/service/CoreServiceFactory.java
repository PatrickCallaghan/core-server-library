package com.heb.core.service;

import java.lang.reflect.Proxy;

import com.heb.core.Application;

public class CoreServiceFactory {
	
	@SuppressWarnings("unchecked")
	public <T> T getCoreServiceProxy(Class<T> interfaces, final T target, Application application, String serviceName) {
		
		return (T) Proxy.newProxyInstance(target.getClass().getClassLoader(), 
				new Class[] { interfaces }, 
				new CoreServiceInvocationHandler(application, target));
	}
}
