package com.heb.core.service;

import java.util.Date;

import com.heb.core.annotation.Audit;
import com.heb.core.annotation.LogRequest;
import com.heb.core.annotation.LogResponse;
import com.heb.core.annotation.Raw;
import com.yammer.metrics.annotation.Metered;
import com.yammer.metrics.annotation.Timed;


public class TestServiceImpl implements TestService{

	@Timed(name="MyLittleA")
	public void a(){}

	@Metered
	public void b(){}

	@Timed
	@Metered(name="CForSomeReason")
	public void c(){}

	@Override
	public void d() {
		// TODO Auto-generated method stub
		
	}

	@Raw
	@Override
	public void e() {
		// TODO Auto-generated method stub
		
	}

	@LogResponse(name="debug")
	@Override
	public Date f() {
		// TODO Auto-generated method stub
		return new Date();
	}

	@LogRequest(name="info")
	@Override
	public void g(String str, Long someLong) {
		// TODO Auto-generated method stub
		
	}

	@Audit
	@Override
	public String h(String str) {
		// TODO Auto-generated method stub
		return "@fasd";
	}

	@LogRequest(name="debug")
	@Override
	public void g(String str) {
		// TODO Auto-generated method stub
		
	}
}
