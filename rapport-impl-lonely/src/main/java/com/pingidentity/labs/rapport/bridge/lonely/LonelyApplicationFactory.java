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
package com.pingidentity.labs.rapport.bridge.lonely;

import java.util.Arrays;
import java.util.List;

import javax.json.JsonArray;
import javax.json.JsonValue;

import com.pingidentity.labs.rapport.Application;
import com.pingidentity.labs.rapport.Peer;
import com.pingidentity.labs.rapport.StateManager;
import com.pingidentity.labs.rapport.spi.ApplicationFactory;
import com.pingidentity.labs.rapport.spi.PeerInstance;

public class LonelyApplicationFactory<A extends Peer, S,T> implements ApplicationFactory<S, T> {
	@SuppressWarnings("rawtypes")
	@Override
	public com.pingidentity.labs.rapport.spi.ApplicationFactory.Initializer newInstance(
			Class<? extends Application> applicationClass, List<? extends Peer> peers, JsonValue localConfiguration,
			JsonValue constitution) {
		if (peers.size() != 1) {
			throw new RuntimeException("lonely implementation needs 1 peer");
		}
		Peer self = peers.get(0);
		return new Initializer<>(applicationClass, self, localConfiguration, constitution);
	}

	public static class Initializer<S, T> implements ApplicationFactory.Initializer {
		private final Application<S,T> application;
		private final Peer self;
		private final JsonValue constitution;
		private LonelyCoordinator<S,T> coordinator;
		private final JsonValue localConfiguration;
		
		@SuppressWarnings("unchecked")
		private Initializer(
				@SuppressWarnings("rawtypes")
				Class<? extends Application> applicationClass,
				Peer self,
				JsonValue localConfiguration,
				JsonValue constitution) {
			try {
				this.application = applicationClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Unable to create application instance " + applicationClass.getSimpleName(), e);
			}
			this.self = self;
			this.constitution = constitution;
			this.localConfiguration = localConfiguration;
		}
		@Override
		public void startStateManager() {
			StateManager<S,T> stateManager = application.createStateManager();
			coordinator = new LonelyCoordinator<S,T>(self, stateManager, constitution, localConfiguration);
		}
		
		public void startInteractor() {
			if (coordinator == null) {
				throw new IllegalStateException("State manager must be started before interactor");
			}
			Thread thread = new Thread(()->application.createInteractor(coordinator));
			thread.start();
		}
	}

	@Override
	public List<? extends Peer> parseJsonPeers(JsonArray object) {
		if (object != null && !object.isEmpty()) {
			throw new RuntimeException("Rapport lonely provider has no peers :'(");
		}
		return Arrays.asList(new PeerInstance("local", new byte[] {0}));
	}
}