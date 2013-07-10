package com.heb.core.service;

import java.util.Date;

import com.heb.core.annotation.Audit;
import com.heb.core.annotation.LogResponse;
import com.heb.core.annotation.Raw;
import com.yammer.metrics.annotation.Metered;
import com.yammer.metrics.annotation.Timed;

public interface TestService {

	@Timed(name="MyLittleA")
	public void a();

	@Metered
	public void b();
	
	@Timed
	@Metered(name="CForSomeReason")
	public void c();
	
	@Raw
	public void d();

	@LogResponse
	public void e();
	
	@Audit
	public Date f();
	

	void g(String str);

	String h(String str);

	void g(String str, Long someLong);
	
}