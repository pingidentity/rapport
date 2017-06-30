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

/** Represents a network address of a peer. */
public interface Peer {
	/**
	 * @return nickname of the peer, for debug display. Required to be unique at any particular point of the
	 * state of the system. Should not be used to identify a peer for authorization.
	 */
	public String getNickname();
	
	/**
	 * @return identifier of the peer, possibly based on a cryptographic identity. Required to be unique at
	 * any particular point of the state of the system. 
	 */
	public byte[] getIdentifier();
	
	boolean equals(Object otherPeer);
	
	int hashCode();
}
