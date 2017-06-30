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

/** Interface for user-created applications, divided into a state manager and interaction model 
 * @param <S> Java type of the state object being maintained by the {@link StateManager} of the application.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by the coordinator and parsed/received by the {@link StateManager}
 */
public interface Application<S,T> {

	/** Initialize interaction model.
	 * 
	 * @param coordinator coordinator between the interactor and the state manager
	 */
	public void createInteractor(Coordinator<S,T> coordinator);

	/** Create a new instance of the application-specific state manager 
	 * 
	 * @return state manager, which takes transactions and created a view of the distributed system state
	 */
	public StateManager<S, T> createStateManager();
}