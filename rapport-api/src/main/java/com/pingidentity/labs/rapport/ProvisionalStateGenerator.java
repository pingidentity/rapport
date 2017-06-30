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

import java.util.List;

/**
 * Optional interface which represents generation of a state object from a transaction which has not had consensus determined yet.
 * 
 * Not all backing implementations of the Bridge API support such provisional messages. If an implementation does not support
 * this functionality, the method will never be called.
 * 
 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by this coordinator and parsed/received by the {@link StateManager}
 */
public interface ProvisionalStateGenerator<S, T> {
	/** Create a new state based on an existing state and received provisional transactions.
	 * 
	 * @param originalState existing state before event is applied
	 * @param transactions an ordered provisional transaction sequence to apply to the state
	 * @return new state, or optionally <code>null</code> to indicate no state change occurred.
	 */
	public S handleProvisionalTransactions(S originalState, List<? extends ProvisionalMessage<T>> transactions); 
}