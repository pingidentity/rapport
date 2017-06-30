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
package com.pingidentity.labs.rapport.bridge.swirlds;


import java.time.Instant;
import java.util.Objects;

import com.pingidentity.labs.rapport.Peer;
import com.pingidentity.labs.rapport.TransactionMessage;

public final class BridgedTransactionMessage<T> implements TransactionMessage<T> {		
	private final Peer  sender;
	private final Instant  timeCreated;
	private final T        transaction;
	
	public BridgedTransactionMessage(Peer sender, Instant timeCreated, T transaction) {
		Objects.requireNonNull(sender);
		Objects.requireNonNull(timeCreated);
		Objects.requireNonNull(transaction);
		
		this.sender = sender;
		this.timeCreated = timeCreated;
		this.transaction = transaction;	
	}
	
	@Override
	public Peer getSender() {
		return sender;
	}

	@Override
	public Instant getConsensusEstablishedTime() {
		return timeCreated;
	}

	@Override
	public T getTransaction() {
		return transaction;
	}
}
