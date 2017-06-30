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

/** A state manager is a combination of the state transformations and the serialization/deserialization of 
 * transaction objects to bytes 
 * 
 * @param <S> Java type of the state object being maintained by this state manager.
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by the coordinator and parsed/received by this state manager.
 */
public interface StateManager<S, T> extends
	StateGenerator<S, T>,
	StateSerializer<S>,
	TransactionSerializer<T> {
}