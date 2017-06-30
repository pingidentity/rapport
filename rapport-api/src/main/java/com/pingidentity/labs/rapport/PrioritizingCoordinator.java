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

import java.util.Collection;
import java.util.List;

/** Specialization of a coordinator that can prioritize sending communication to other peers. While this
 * information still must have consensus before it is part of consensus state, if those peers are using a
 * {@link ProvisionalStateGenerator} to represent non-consensus state they may be able to represent your
 * message sooner
 *
 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by this coordinator and parsed/received by the {@link StateManager}
 */
public interface PrioritizingCoordinator<S, T> extends Coordinator<S,T> {
	/** Enqueue a sequence of transactions for transmission to other peers and incorporation into state. Specifies peers whom
	 *  delivery priority should be given to, if possible. 
	 *  
	 * @param transactions list of transactions to send
	 * @param hintedRecipients collection of recipient peers to prioritize
	 */
	public void queueTransactions(List<? extends T> transactions, Collection<? extends Peer> hintedRecipients);

}
