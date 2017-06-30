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

import java.util.List;

import javax.json.JsonValue;

/**
 * Represents generation of a state object, either from an initial constitution or as a modification of a state 
 * object based on a received event
 * 
 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by the coordinator and parsed/received by this StateGenerator.
 */
public interface StateGenerator<S,T> {
	/** Create a state based on the constitution of the system. Must not return <code>null</code> 
	 * 
	 * @param initialPeers given peers within the constitution. This list may be the entire list of initial
	 * peers, or may be a discovery list to join a peer network
	 * 
	 * @param constitution initial constitution of the system.
	 * 
	 * @return initial state object
	 */
	public S createInitialState(List<? extends Peer> initialPeers, JsonValue constitution);
	
	/** Create a new state based on an existing state and a received event.
	 * 
	 * @param originalState existing state before event is applied
	 * @param transactions an ordered transaction sequence to apply to the state
	 * @return new state, or optionally <code>null</code> to indicate no state change occurred.
	 */
	public S handleTransactions(S originalState, List<? extends TransactionMessage<T>> transactions); 
}