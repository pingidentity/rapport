/* Copyright 2017 Ping Identity Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
package com.pingidentity.labs.rapport;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import com.pingidentity.labs.rapport.spi.ApplicationFactory;

/** Class to execute applications based on registered ApplicationFactory instances. This class is only meant to be
 * used in systems where the developer (not the underlying system) is responsible for instantiating the 
 * application. */
public final class ApplicationRunner {
	@SuppressWarnings("rawtypes")
	private final Class<? extends Application> applicationClass;
	
	@SuppressWarnings("rawtypes")
	private ApplicationRunner(Class<? extends Application> application) {
		this.applicationClass = application;
	}
	
	/** create an instance to run a particular application 
	 * 
	 * @param applicationClass class of application to initialize
	 * @return ApplicationRunner instance 
	 */
	@SuppressWarnings("rawtypes")
	public static ApplicationRunner newInstance(Class<? extends Application> applicationClass) {
		return new ApplicationRunner(applicationClass);
	}

	/**
	 * Initialize the application runner based on a JSON configuration
	 * 
	 * @param configurationData JSON object containing configuration data
	 */
	public void init(JsonObject configurationData) {
		ApplicationFactory<?,?> factory = getFactory();
		JsonValue constitutionData = configurationData.get("constitution");
		JsonValue localConfiguration = configurationData.get("localConfiguration");
		List<? extends Peer> addresses = getPeers(factory, configurationData);
		init(addresses, localConfiguration, constitutionData);
	}

	private List<? extends Peer> getPeers(ApplicationFactory<?,?> factory, JsonObject configurationData) {
		JsonArray peerData = configurationData.getJsonArray("peers");
		return factory.parseJsonPeers(peerData);
	}
	
	private ApplicationFactory<?,?> getFactory() {
		@SuppressWarnings("rawtypes")
		ServiceLoader<ApplicationFactory> factoriesLoader = ServiceLoader.load(ApplicationFactory.class);
		List<ApplicationFactory<?,?>> factories = new ArrayList<>();
		for (ApplicationFactory<?,?> factory : factoriesLoader) {
			factories.add(factory);
		}
		
		if (factories.isEmpty()) {
			throw new IllegalStateException("No ApplicationFactory services available");
		}
		if (factories.size() > 1) {
			throw new IllegalStateException("Multiple ApplicationFactory services installed. " +
					"Unable to select which one to use");
		}
		return factories.get(0);
	}
	/** initialize both the state management and interaction layers of the given application.
	 * 
	 * @param peers Peer objects appropriate for back-end
	 * @param localConfiguration local configuration for application, including multiple instances to support
	 * multiple application instances for multiple local peers
	 * @param constitution starting constitution of this network
	 */
	public void init(List<? extends Peer> peers, JsonValue localConfiguration, JsonValue constitution) {
		ApplicationFactory<?,?> factory = getFactory();
		ApplicationFactory.Initializer initializer = factory.newInstance(applicationClass, peers, localConfiguration, constitution);
		initializer.startStateManager();
		initializer.startInteractor();
	}
}