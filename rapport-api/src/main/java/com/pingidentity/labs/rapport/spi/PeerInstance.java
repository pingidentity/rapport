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
package com.pingidentity.labs.rapport.spi;

import java.util.Arrays;
import java.util.Objects;

import com.pingidentity.labs.rapport.Peer;

/** A default peer implementation (if provider has no other requirements.)
 * 
 * May be used as a superclass for a backend implementation's Peer instances.
 */
public class PeerInstance implements Peer {
	private final String nickname;
	private final byte[] identifier;
	
	/**
	 * Create a new peer instance.
	 * 
	 * @param nickname nickname of this peer. Must be unique locally
	 * @param identifier binary identifier of this peer. Must be unique locally
	 */
	public PeerInstance(String nickname, byte[] identifier) {
		Objects.requireNonNull(nickname);
		Objects.requireNonNull(identifier);
		
		this.nickname = nickname;
		this.identifier = Arrays.copyOf(identifier, identifier.length);
	}
	
	private static String toHex(byte[] binary) {
		StringBuilder builder = new StringBuilder();
		for (byte b : binary) {
			builder.append(String.format("%2x", b));
		}
		return builder.toString();
	}

	/**
	 * Create a peer, using the binary identifier as the local nickname. Does not currently truncate the
	 * nickname to a prefix of the binary identifier.
	 * 
	 * @param identifier binary identifier. Must be unique
	 */
	public PeerInstance(byte[] identifier) {
		this(toHex(identifier), identifier);
	}
		
	@Override
	public String getNickname() {
		return nickname;
	}

	@Override
	public byte[] getIdentifier() {
		return Arrays.copyOf(identifier, identifier.length);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof Peer)) {
			return false;
		}
		
		Peer rhs = (Peer) other;
		return (nickname.equals(rhs.getNickname()) && Arrays.equals(identifier, rhs.getIdentifier()));
	}
	
	@Override
	public int hashCode() {
		return 31 * Arrays.hashCode(identifier) ^ nickname.hashCode();
	}
}