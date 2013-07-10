package com.heb.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.heb.core.Application;
import com.heb.core.service.CoreServiceFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-core-spring-config.xml" })
public class TestCoreServiceFactory {

	@Autowired
	public Application application;

	@Test
	public void testCreate() {

		TestService testService = new CoreServiceFactory().getCoreServiceProxy(TestService.class, new TestServiceImpl(), application,
				"ServiceName");

		for (int i = 0; i < 5000; i++) {
			testService.a();
			testService.b();
			testService.c();
			testService.d();
			testService.e();
			testService.f();
			testService.g("This is a test");
			testService.g("This is a test", new Long(123123));
			testService.h("Another TEst with result");
		}

		System.out.println(application.getHealthCheckRegistry().runHealthChecks());
	}
}
