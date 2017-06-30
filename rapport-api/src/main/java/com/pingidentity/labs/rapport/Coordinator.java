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

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import javax.json.JsonValue;

/** Coordinates between a local instance of the application and the rest of the peers. It does this in several ways
 * ways:
 * 
 * 1. It provides a way to read a snapshot state of the distributed system
 * 2. It provides a way to enqueue a transaction to be sent to the rest of the distributed system.
 * 3. It allows an application to see information on itself as a peer as well as local configuration
 *    controlling how it should act.
 *    
 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by this coordinator and parsed/received by the {@link StateManager}
 */
public interface Coordinator<S, T> {
	/** Get a reference to this peer within the distributed system. As it is possible to have a model where
	 * the peers can change over time, the responsibility for maintaining and exposing a list of peers if
	 * needed falls onto the {@link StateManager}s. 
	 * 
	 * @return peer of local application instance */
	Peer getSelf();

	/** Retrieve and operate on a snapshot of the current system state, returning a value. The semantics of this
	 *  state (such as whether the state is authoritative) is determined by the implementation of the StateManager
	 *  of the Application.
	 *  
	 * @param <R> return class of the included function
	 * @param stateConsumer function consuming a state snapshot and returning a value as a result
	 * @return result from state consumer
	 */
	public<R> R withState(Function<? super S, ? extends R> stateConsumer);

	/** Retrieve and operate on a snapshot of the current system state. The semantics of this  state (such
	 *  as whether the state is authoritative) is determined by the implementation of the StateManager
	 *  of the Application.
	 *  
	 * @return A snapshot of the system state
	 */
	S getState();

	/** Enqueue a transaction for transmission to other peers and incorporation into state 
	 * 
	 * @param transaction transaction object to send
	 */
	default public void queueTransaction(T transaction) {
		queueTransactions(Collections.singletonList(transaction));
	}

	/** Enqueue a sequence of transactions for transmission to other peers and incorporation into state 
	 * 
	 * @param transactions an ordered list of transactions to send
	 */
	public void queueTransactions(List<? extends T> transactions);
	
	/**
	 * @return a JSON value representing the local configuration
	 */
	public JsonValue getLocalConfiguration();
}
