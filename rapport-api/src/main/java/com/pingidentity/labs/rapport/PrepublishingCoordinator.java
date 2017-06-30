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

import java.util.EventListener;
import java.util.EventObject;

/**
 * Platforms may implement this interface in order to expose a mechanism to get pre-publication events. This
 * may allow for efficiency as multiple transactions to share with other peers are bundled into a single
 * transactional 'unit', consolidating network writes and possibly allowing for cryptographic computation 
 * savings.
 * 
 * Backends may place additional requirements on prepublish event listeners, as a long-running listener
 * may hold up network communications.
 *
 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by this coordinator and parsed/received by the {@link StateManager}
 */
public interface PrepublishingCoordinator<S, T> extends Coordinator<S, T> {

	/**
	 * Add a new prepublish event listener
	 * @param listener prepublish event listener to add
	 */
	public void addPrepublishEventListener(PrepublishEventListener<S,T> listener);
	
	/**
	 * Remove existing prepublish event listener
	 * @param listener prepublish event listener to remove
	 */
	public void removePrepublishEventListener(PrepublishEventListener<S,T> listener);
	
	/**
	 * A prepublish event is sent to signify to a listener that communication is about to happen with other
	 * peers.
	 * 
	 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
	 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
	 * by this coordinator and parsed/received by the {@link StateManager}
	 */
	public class PrepublishEvent<S,T> extends EventObject {
		private static final long serialVersionUID = 1L;

		/**
		 * Create a new prepublish event around a coordinator
		 * @param source coordinator
		 */
		public PrepublishEvent(Coordinator<S,T> source) {
			super(source);
		}

		/**
		 * @return the source coordinator
		 */
		@SuppressWarnings("unchecked")
		public Coordinator<S,T> getCoordinator() {
			return (Coordinator<S,T>) getSource();
		}
	}
	
	/**
	 * Listener to be called before publishing of new transactions or a peer state update.
	 *
	 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
	 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
	 * by this coordinator and parsed/received by the {@link StateManager}
	 */
	public interface PrepublishEventListener<S,T> extends EventListener {
		/**
		 * backend is about to communicate with other peers.
		 * @param preEvent event sourced from the coordinator associated with the communicating backend
		 */
		public void beforePublish(PrepublishEvent<S,T> preEvent);
	}
}
