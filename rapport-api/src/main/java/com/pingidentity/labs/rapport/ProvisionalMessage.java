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


import java.time.Instant;

/** Represents a single received transaction which has not yet had consensus established. For transactions
 * which have consensus established, see {@link TransactionMessage}. 
 * 
 * If a backend supports non-consensus messages, this message is sent by having your {@link StateGenerator}
 * implement {@link ProvisionalStateGenerator}.
 * 
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by the coordinator and parsed/received by the {@link StateManager}
 * */
public interface ProvisionalMessage<T> {
	/**
	 * The original sending/creating peer of this transaction
	 * 
	 * @return sending/creating peer
	 */
	public Peer    getSender();
	
	/**
	 * The time the transaction was initially received/seen by the local backend. 
	 * 
	 * @return local instant time received
	 */
	public Instant getTimeReceived();
	
	/**
	 * Retrieve the transactional message, represented as a java class
	 * 
	 * @return transaction object
	 */
	public T       getTransaction();
}