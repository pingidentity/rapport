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

import java.time.Clock;
import java.time.Instant;

/** Represents a single received Transaction with consensus on order and parameters. Some backends may also
 * support non-consensus/pre-consensus messasge reporting via {@link ProvisionalMessage} and
 * {@link ProvisionalStateGenerator}
 * 
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by the coordinator and parsed/received by the {@link StateManager}
 * 
 */
public interface TransactionMessage<T> {
	/**
	 * The original sending/creating peer of this transaction
	 * 
	 * @return sending/creating peer
	 */
	public Peer    getSender();


	/**
	 * A time given by the backend for when consensus was established. This time is based on the consensus
	 * {@link Clock}, not necessarily the local system clock. Care should be taken to understand the
	 * ramifications of clock drift if comparing to instances of local time, such as 
	 * {@link ProvisionalMessage#getTimeReceived() }
	 * 
	 * A consensus backend should guarantee that TransactionMessage instances are received in chronological
	 * order based on this time.
	 * 
	 * @return consensus-clock consensus established time
	 */
	public Instant getConsensusEstablishedTime();
	
	/**
	 * Retrieve the transactional message, represented as a java class
	 * 
	 * @return transaction object
	 */
	public T       getTransaction();
}
