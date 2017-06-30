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

import com.swirlds.platform.Event;

/** Adapter wrapper around swirlds event to expose API w/o requiring swirlds to be exposed */
public class BridgedEvent {
	private final Event wrapped;
	
	BridgedEvent(Event toWrap) {
		Objects.requireNonNull(toWrap);
		wrapped = toWrap;
	}
	
	public long getConsensusOrder() {
		return wrapped.getConsensusOrder();
	}
	Instant getConsensusTimestamp() {
		return wrapped.getConsensusTimestamp();
	}
	
	public long getCreatorParticipantId() {
		return wrapped.getCreatorId();
	}
	public long getCreatorEventSequenceNumber() {
		return wrapped.getCreatorSeq();
	}
	
	public long getGeneration() {
		return wrapped.getGeneration();
	}
	
	public long getOtherId() {
		return wrapped.getOtherId();
	}
	public Event getOtherParentEvent() {
		return wrapped.getOtherParent();
	}
	
	public long getOtherSeq() {
		return wrapped.getOtherSeq();
	}
	
	public long getRoundCreated() {
		return wrapped.getRoundCreated();
	}
	public long getRoundReceived() {
		return wrapped.getRoundReceived();
	}
	
	public Event getSelfParentEvent() {
		return wrapped.getSelfParent();
	}
	public byte[] getSignature() {
		return wrapped.getSignature();
	}
	public Instant getTimeCreated() {
		return wrapped.getTimeCreated();
	}
	
	public boolean isConsensus() {
		return wrapped.isConsensus();
	}
	
	public boolean isFameDecided() {
		return wrapped.isFameDecided();
	}
	
	public boolean isFamous() {
		return wrapped.isFamous();
	}

	public boolean isWitness() {
		return wrapped.isWitness();
	}
	
	public String toString() {
		return wrapped.toString();
	}
}
