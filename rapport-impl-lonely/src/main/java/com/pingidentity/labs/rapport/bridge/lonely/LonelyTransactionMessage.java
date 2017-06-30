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


import java.time.Instant;

import com.pingidentity.labs.rapport.Peer;
import com.pingidentity.labs.rapport.TransactionMessage;

public final class LonelyTransactionMessage<T> implements TransactionMessage<T> {		
	private final Peer  recipient;
	private final Instant  timeCreated;
	private final T        transaction;
	
	public LonelyTransactionMessage(Peer recipient, Instant timeCreated, T transaction) {
		this.recipient = recipient;
		this.timeCreated = timeCreated;
		this.transaction = transaction;	
	}
	
	@Override
	public Peer getSender() {
		return recipient;
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
