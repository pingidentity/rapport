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
package com.pingidentity.labs.rapport.spi;

import java.util.List;
import javax.json.JsonArray;
import javax.json.JsonValue;

import com.pingidentity.labs.rapport.Application;
import com.pingidentity.labs.rapport.Coordinator;
import com.pingidentity.labs.rapport.Peer;
import com.pingidentity.labs.rapport.StateManager;

/** 
 * Service Provider interface to handle Application initialization based on a provided implementation of this API.
 * Separates starting of the state manager and interactor for environments where headless operation is appropriate.
 * 
 * An ApplicationFactory instance can be registered by adding a service descriptor to the JAR package - in
 * `META-INF/services/com.pingidentity.labs.rapport.spi.ApplicationFactory`
 * 
 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by your backend {@link Coordinator} and parsed/received by the {@link StateManager}
 */
public interface ApplicationFactory<S,T> {
	/**
	 * Create a new ApplicationFactory around an application implementation instance, a list of peer addresses,
	 * and the system constitution.
	 *
	 * @param application class of application to initialize
	 * @param peers list of peers
	 * @param localConfiguration local configuration of this peer
	 * @param constitution initial configuration of the distributed system
	 * @return Initializer object which can start the state manager and application
	 */
	public Initializer newInstance(
			@SuppressWarnings("rawtypes") 
			Class<? extends Application> application, 
			List<? extends Peer> peers, 
			JsonValue localConfiguration,
			JsonValue constitution);

	/**
	 * Parse a JSON array into a list of peers within the system. The format of the peers is mostly 
	 * determined by the backend, although the file format description gives details on expected/required
	 * keys.
	 * 
	 * @param array JSON array of peers
	 * @return list of peer objects
	 */
	public List<? extends Peer> parseJsonPeers(JsonArray array);
	
	/**
	 * Initializer object, returned by the backend, to allow java code to control the initialization
	 * of the state manager and interactor instances. 
	 */
	public interface Initializer {
		/** Start state manager. State manager must be started before Interactor */
		public void startStateManager();
		/** Start user/system interaction process. State manager must be started before Interactor */
		public void startInteractor();
	}
}