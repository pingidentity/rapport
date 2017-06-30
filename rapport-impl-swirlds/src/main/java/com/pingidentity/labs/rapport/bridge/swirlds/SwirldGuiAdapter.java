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

import com.swirlds.platform.Address;
import com.swirlds.platform.Browser;
import com.swirlds.platform.Platform;
import com.swirlds.platform.SwirldMain;
import com.swirlds.platform.SwirldState;

public class SwirldGuiAdapter<S,T> implements SwirldMain {
	public static String stateClassName = SwirldStateManagerAdapter.class.getName();
	private BridgedSwirldPlatformInstanceCoordinator<S, T> platformInstance;

	@Override
	public void run() {
		platformInstance.getAdapter().getApplication().createInteractor(platformInstance);
		// Swirld build currently quits Java if any main thread exits; add an endless loop
		while(true) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) { }
		}
	}
	
	public void preEvent() {
		platformInstance.emitPrepublishEvent();
	}

	@Override
	public void init(Platform platform, int myAddressBookIndex) {
		BridgedPlatformConfiguration configuration = 
				BridgedPlatformConfiguration.fromParameters(platform.getParameters());
		Address address = platform.getState().getAddressBookCopy().getAddress(myAddressBookIndex);
		this.platformInstance = new BridgedSwirldPlatformInstanceCoordinator<>(platform, myAddressBookIndex, configuration.getLocalConfiguration().get(address.getSelfName()));
	}

	@Override
	public SwirldState newState() {
		return new SwirldStateManagerAdapter<S,T>();
	}
	
	public static void main(String[] args) {
		Browser.main(args);
	}
}