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

import java.nio.charset.Charset;

import com.pingidentity.labs.rapport.Peer;

public class BridgedPeer implements Peer {
	public final com.swirlds.platform.Address swirldsAddress;
	
	public BridgedPeer(com.swirlds.platform.Address swirldsAddress) {
		this.swirldsAddress = swirldsAddress;
	}
	@Override
	public String getNickname() {
		return swirldsAddress.getNickname();
	}

	@Override
	public byte[] getIdentifier() {
		// TODO change to public key once public key is usable
		return swirldsAddress.getSelfName().getBytes(Charset.forName("UTF-8"));
	}
}
