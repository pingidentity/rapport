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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.json.JsonValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.labs.rapport.Peer;
import com.pingidentity.labs.rapport.PrepublishingCoordinator;
import com.pingidentity.labs.rapport.StateManager;

public class LonelyCoordinator<S,T> implements PrepublishingCoordinator<S, T> {
	
	private static class TransactionRequest<T> {
		private final Instant enqueueInstant;
		private final Collection<? extends T> transactions;
		
		public TransactionRequest(Collection<? extends T> transactions) {
			this.enqueueInstant = Instant.now();
			this.transactions = transactions;
		}

		public Collection<? extends T> getTransactions() {
			return transactions;
		}

		@SuppressWarnings("unused")
		public Instant getEnqueueInstant() {
			return enqueueInstant;
		}
	}
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final StateManager<S, T> manager;
	private final AtomicReference<S> state;
	private final Peer address;
	private final JsonValue localConfiguration;
	private final List<PrepublishEventListener<S,T>> listeners;
	public LonelyCoordinator(Peer address, StateManager<S, T> manager, JsonValue constitution, JsonValue localConfiguration) {
		this.manager = manager;
		this.address = address;
		this.state = new AtomicReference<>(manager.createInitialState(Collections.singletonList(address), constitution));
		this.localConfiguration = localConfiguration;
		this.listeners = new ArrayList<>();
	}

	public S getState() {
		return state.get();
	}
	
	public void releaseState() {
	}
	
	public void withState(Consumer<? super S> stateConsumer) {
		S state;
		try {
			state = getState();
			stateConsumer.accept(state);
		}
		finally {
			releaseState();
		}
	}

	public<R> R withState(Function<? super S, ? extends R> stateConsumer) {
		S state;
		try {
			state = getState();
			return stateConsumer.apply(state);
		}
		finally {
			releaseState();
		}
	}

	@Override
	public Peer getSelf() {
		return address;
	}

	private void handleTransaction(TransactionRequest<T> tr) {
		Instant now = Instant.now();
		
		List<LonelyTransactionMessage<T>> events = tr.getTransactions().stream().
				map((tx)-> {
					return new LonelyTransactionMessage<T>(address, now, tx);
				}).
				collect(Collectors.toList());
		boolean success = false;
		while(!success) {
			S oldState = state.get();
			S newState = manager.handleTransactions(oldState, events);
			success = state.compareAndSet(oldState, newState);
		}
	}
	@Override
	public void queueTransactions(List<? extends T> transactions) {
		TransactionRequest<T> tr = new TransactionRequest<>(transactions);
		PrepublishEvent<S,T> preEvent = new PrepublishEvent<>(this);
		listeners.forEach( (listener) -> {
			try {
				listener.beforePublish(preEvent);
			}
			catch (Exception e) {
				log.warn("Exception while informing listener of prepublish event. listener={}", listener, e);
			}
		});
		handleTransaction(tr);
	}

	@Override
	public JsonValue getLocalConfiguration() {
		return localConfiguration;
	}

	@Override
	public void addPrepublishEventListener(PrepublishEventListener<S,T> listener) {
		listeners.add(listener);
	}

	@Override
	public void removePrepublishEventListener(PrepublishEventListener<S, T> listener) {
		listeners.remove(listener);
	}
}
