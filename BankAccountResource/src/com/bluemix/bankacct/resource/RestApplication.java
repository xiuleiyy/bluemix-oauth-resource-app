package com.bluemix.bankacct.resource;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class RestApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> classes = new HashSet<Class<?>>();
		classes.add(CustomerResource.class);
		return classes;
	}
	
}
