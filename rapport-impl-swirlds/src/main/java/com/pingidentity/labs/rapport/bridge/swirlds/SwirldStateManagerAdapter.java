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


import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.pingidentity.labs.rapport.Peer;
import com.pingidentity.labs.rapport.Application;
import com.pingidentity.labs.rapport.ProvisionalMessage;
import com.pingidentity.labs.rapport.ProvisionalStateGenerator;
import com.pingidentity.labs.rapport.StateManager;
import com.pingidentity.labs.rapport.TransactionMessage;
import com.swirlds.platform.AddressBook;
import com.swirlds.platform.FCDataInputStream;
import com.swirlds.platform.FCDataOutputStream;
import com.swirlds.platform.FastCopyable;
import com.swirlds.platform.Platform;
import com.swirlds.platform.SwirldState;

public class SwirldStateManagerAdapter<S, T> implements SwirldState {
	private StateManager<S, T>		stateManager;
	private AtomicReference<S>		stateRef;
	private Application<S, T>		application;
	private List<? extends Peer> addresses;

	public SwirldStateManagerAdapter() { }

	@SuppressWarnings("unchecked")
	@Override
	public void init(Platform platform, AddressBook addressBook) {
		BridgedPlatformConfiguration configuration = 
				BridgedPlatformConfiguration.fromParameters(platform.getParameters());
		try {
			this.application = (Application<S,T>) configuration.getApplicationClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Unable to instantiate class " + configuration.getApplicationClass().getName(), e);
		}
		this.stateManager = application.createStateManager();

		addresses = translateAddressBook(addressBook);
		this.stateRef = new AtomicReference<>(stateManager.createInitialState(addresses, configuration.getConstitution()));
	}

	private List<? extends Peer> translateAddressBook(AddressBook addressBook) {
		ArrayList<Peer> results = new ArrayList<>(addressBook.getSize());
		for (int i = 0; i < addressBook.getSize();i++) {
			results.add(new BridgedPeer(addressBook.getAddress(i)));
		}
		return results;
	}

	public AddressBook untranslateAddressBook(List<? extends Peer> addresses) {
		return new AddressBook(
				addresses.stream().
					map(BridgedPeer.class::cast).
					map((addr) -> addr.swirldsAddress).
					collect(Collectors.toList()));
	}
	@SuppressWarnings("unchecked")
	protected SwirldStateManagerAdapter(SwirldState old) {
		this();
		SwirldStateManagerAdapter<S, T> other = (SwirldStateManagerAdapter<S, T>) old;
		this.stateManager = other.stateManager;
		this.addresses    = other.addresses;
		this.application  = other.application;
		this.stateRef = new AtomicReference<>(other.stateRef.get());
   }

	@SuppressWarnings("unchecked")
	@Override
	public void handleTransaction(long id, boolean frozen, Instant timeCreated, byte[] trans, com.swirlds.platform.Address addressToAdd) {
		Collection<T> transactionObjects = stateManager.parseTransactions(trans);
		Peer recipient = addresses.get((int)id);
		if (frozen) {
			List<TransactionMessage<T>> transactions = transactionObjects.stream().
					map((tx) -> new BridgedTransactionMessage<T>(recipient, timeCreated, tx)).
					collect(Collectors.toList());
			
			boolean success = false;
			while(!success) {
				S oldState = stateRef.get();
				S newState = stateManager.handleTransactions(oldState, transactions);
				if (newState != null) {
					success = stateRef.compareAndSet(oldState, newState);
				}
			}
		}
		else if (stateManager instanceof ProvisionalStateGenerator) {
			List<ProvisionalMessage<T>> transactions = transactionObjects.stream().
					map((tx) -> new BridgedProvisionalMessage<T>(recipient, timeCreated, tx)).
					collect(Collectors.toList());
			
			boolean success = false;
			while(!success) {
				S oldState = stateRef.get();
				S newState = ((ProvisionalStateGenerator<S,T>)stateManager).handleProvisionalTransactions(oldState, transactions);
				if (newState != null) {
					success = stateRef.compareAndSet(oldState, newState);
				}
			}
			
		}
	}
	
	StateManager<S, T> getStateManager() {
		return stateManager;
	}

	@Override
	public void copyFrom(SwirldState oldState) {
		@SuppressWarnings("unchecked")
		SwirldStateManagerAdapter<S, T> other = (SwirldStateManagerAdapter<S, T>) oldState;
		stateManager = other.stateManager;
		addresses = other.addresses;
		stateRef = new AtomicReference<>(other.stateRef.get());
	}
	
	public S getState() {
		return stateRef.get();
	}

	public Application<S, T> getApplication() {
		return application;
	}

	public List<? extends Peer> getAddresses() {
		return addresses;
	}

	@Override
	public void copyFrom(FCDataInputStream fcdis) throws IOException {
		stateRef.set(stateManager.deserializeState(fcdis));
	}

	@Override
	public void copyTo(FCDataOutputStream fcdos) throws IOException {
		stateManager.serializeState(stateRef.get(), fcdos);
	}

	@Override
	public AddressBook getAddressBookCopy() {
		return untranslateAddressBook(addresses);
	}

	@Override
	public void freeze() {
		// do nothing
	}

	@Override
	public FastCopyable copy() {
		return new SwirldStateManagerAdapter<>(this);
	}
}
