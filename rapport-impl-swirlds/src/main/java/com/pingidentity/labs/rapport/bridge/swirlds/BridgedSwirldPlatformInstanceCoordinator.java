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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.json.JsonValue;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pingidentity.labs.rapport.Peer;
import com.pingidentity.labs.rapport.PrepublishingCoordinator;
import com.pingidentity.labs.rapport.PrioritizingCoordinator;
import com.swirlds.platform.AddressBook;

public class BridgedSwirldPlatformInstanceCoordinator<S,T> implements PrepublishingCoordinator<S,T>, PrioritizingCoordinator<S, T> {
	private final com.swirlds.platform.Platform platform;
	private final int selfId;
	private final JsonValue localConfiguration;
	private final List<PrepublishEventListener<S,T>> listeners;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	BridgedSwirldPlatformInstanceCoordinator(com.swirlds.platform.Platform platform, int selfId, JsonValue localConfiguration) {
		Objects.requireNonNull(platform);
		this.platform = platform;
		this.selfId = selfId;
		this.localConfiguration = localConfiguration;
		this.listeners = new ArrayList<>();
	}
	@SuppressWarnings("unchecked")
	SwirldStateManagerAdapter<S,T> getAdapter() {
		return (SwirldStateManagerAdapter<S,T>)platform.getState();
	}

	@Override
	public S getState() {
		return getAdapter().getState();
	}
	
	public void withState(Consumer<? super S> stateConsumer) {
		S state = getState();
		stateConsumer.accept(state);
	}

	public<R> R withState(Function<? super S, ? extends R> stateConsumer) {
		S state = getState();
		return stateConsumer.apply(state);
	}

	@Override
	public void queueTransactions(List<? extends T> transactions, Collection<? extends Peer> hintedRecipients) {
		byte[] bytes = getAdapter().getStateManager().serializeTransactions(transactions);
		long hintIds[] = null;
		if (hintedRecipients != null) {
			ArrayList<Integer> recipients = new ArrayList<>();
			AddressBook addressBook = getAdapter().getAddressBookCopy();
			for (Peer hintedRecipient : hintedRecipients) {
				for (int i = 0; i < addressBook.getSize(); i++) {
					String nickname = addressBook.getAddress(i).getNickname();
					if (nickname.equals(hintedRecipient.getNickname())) {
						recipients.add(i);
						continue;
					}
				}
			}
			hintIds = recipients.stream().mapToLong(Integer::intValue).toArray();
		}
		platform.createTransaction(bytes, hintIds);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Peer getSelf() {
		return ((SwirldStateManagerAdapter<S, T>)platform.getState()).getAddresses().get(this.selfId);
	}
	
	@Override
	public JsonValue getLocalConfiguration() {
		return localConfiguration;
	}

	// Begin expose of Swirld specific API
	
	// Current return an opaque object - there is no mechanism to dismiss or alter
	// the console
	public Object createConsole(boolean visible) {
		return platform.createConsole(visible);
	}
	
	// Create a window for use in locally concurrent UI demonstrations
	public JFrame createWindow(boolean visible) {
		return platform.createWindow(visible);
	}
	
	public String getAbout() {
		return platform.getAbout();
	}
	
	public void setAbout(String about) {
		platform.setAbout(about);
	}

	public List<BridgedEvent> getAllEvents() {
		return java.util.stream.Stream.of(platform.getAllEvents()).map(BridgedEvent::new).collect(Collectors.toList());
	}
	
	public double getEventsPerSecond() {
		return platform.getEventsPerSecond();
	}
	
	public BridgedEvent getLastEvent(Peer address) {
		return Optional.ofNullable(
				platform.getLastEvent(((BridgedPeer)address).swirldsAddress.getId()))
				.map(BridgedEvent::new)
				.orElse(null);
	}
	
	public long getNumberOfTransactions() {
		return platform.getNumTrans();
	}
	
	public String[] getParameters() {
		return platform.getParameters();
	}
	
	public long getSleepAfterSync() {
		return platform.getSleepAfterSync();
	}
	
	public void setSleepAfterSync(long millis) {
		platform.setSleepAfterSync(millis);
	}
	
	public double getTransactionsPerSecond() {
		return platform.getTransPerSecond();
	}
	
	public double getAverageCreatedConsensusTime() {
		return platform.getAvgCreatedConsensusTime();
	}

	public double getAverageReceivedConsensusTime() {
		return platform.getAvgReceivedConsensusTime();
	}

	public double getBadEventsPerSecond() {
		return platform.getBadEventsPerSecond();
	}
	
	public double getDuplicateEventsPercentage() {
		return platform.getDuplicateEventsPercentage();
	}
	
	public double getDuplicateEventsPerSecond() {
		return platform.getDuplicateEventsPerSecond();
	}
	
	public long[] getLastSequenceIdentifierByCreator() {
		return platform.getLastSeqByCreator();
	}
	public void emitPrepublishEvent() {
		PrepublishEvent<S, T> event = new PrepublishEvent<>(this);
		listeners.forEach((listener) -> {
			try {
				listener.beforePublish(event);
			}
			catch (Exception e) {
				log.warn("Exception in prepublish event listener {}", listener, e);
			}
		});
		
	}
	@Override
	public void addPrepublishEventListener(PrepublishEventListener<S, T> listener) {
		listeners.add(listener);
		
	}
	@Override
	public void removePrepublishEventListener(PrepublishEventListener<S, T> listener) {
		listeners.remove(listener);
	}
	@Override
	public void queueTransactions(List<? extends T> transactions) {
		queueTransactions(transactions, null);
	}	
}