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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Responsible for translating state objects to and from a data stream to be shared with other
 * participants
 * @param <S> Java type of the state object being maintained by the {@link StateManager}
 */
public interface StateSerializer<S> {
	/**
	 * Given a data source which contains a serialized version of the state, deserialize that state into
	 * a state object.
	 * 
	 * This state data may come from an external source, so its strongly recommended to do additional
	 * validation, and to avoid using java serialization - or any other meechanisms which may allow either
	 * arbitrary classes or bytecode-based creation of Java classes
	 * 
	 * @param dataInput data source
	 * @return deserialized state object
	 * @throws IOException error in the data source, including deserialization errors
	 */
	public S deserializeState(DataInput dataInput) throws IOException;
	
	/**
	 * Given a data output object, serialize the state object. This state object should be canonical -
	 * any peer serializing a state object created from the same sequence of transactions should result in
	 * the same serialized binary data.
	 * 
	 * @param state state object to serialize
	 * @param dataOutput data output to serialize to
	 * @throws IOException error in the data output, or unsupported state configuration for serialization
	 */
	public void serializeState(S state, DataOutput dataOutput) throws IOException;
}