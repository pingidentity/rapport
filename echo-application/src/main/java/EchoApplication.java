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
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.json.JsonValue;

import org.slf4j.*;

import com.pingidentity.labs.rapport.*;

/** The following is a simple echo application. It was inspired by the "Hello, Swirlds" application which
 * Ships with the Swirlds SDK.
 * 
 * This application simply has peers broadcast a message containing their own local name as they
 * become available. Even if multiple peers come available at the same time, all instances should see
 * and output the messages in the same order, as the order is determined by the consensus system.
 * 
 * As the "lonely" backend only has itself, it merely outputs its own name then waits for friends that will
 * never come.
 */
public class EchoApplication implements Application<
	List<String>, /* State, list of messages */ 
	String> { /* String, a message */

	private static final Logger log = LoggerFactory.getLogger(EchoApplication.class);
	
	@Override
	public void createInteractor(Coordinator<List<String>, String> coordinator) {
		System.out.println("Hello! Starting up as " + coordinator.getSelf().getNickname());
		coordinator.queueTransaction(coordinator.getSelf().getNickname());
		// For each new message, print out our current state.
		List<String> previousState = null;
		while(true) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				// do nothing
			}
			List<String> currentState = coordinator.getState();
			if (currentState.equals(previousState))
				continue;
			previousState = currentState;
			System.out.println("State of system changed. The following messages have been received: ");
			System.out.println("\t" + currentState);
		}
	}

	@Override
	public StateManager<List<String>, String> createStateManager() {
		return new StateManager<List<String>, String>() {

			// StateGenerator implementation. Starting from an empty list, append any strings received
			// to the end of that list
			@Override
			public List<String> createInitialState(List<? extends Peer> initialAddresses, JsonValue constitution) {
				return Collections.unmodifiableList(new ArrayList<>());
			}

			@Override
			public List<String> handleTransactions(List<String> originalState,
					List<? extends TransactionMessage<String>> event) {
				ArrayList<String> copy = new ArrayList<>(originalState);
				copy.addAll(event.stream().map(TransactionMessage::getTransaction).collect(Collectors.toList()));
				return Collections.unmodifiableList(copy);
			}

			// TransactionSerializer implementation. For a given set of transactions,  
			@Override
			public byte[] serializeTransactions(List<? extends String> transactions) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					DataOutputStream dos = new DataOutputStream(baos);
					dos.writeInt(transactions.size());
					for (String transaction: transactions) { 
						dos.writeUTF(transaction);
					}
					dos.close();
					return baos.toByteArray();
				}
				catch (IOException e) {
					return null;
				}
			}

			@Override
			public List<String> parseTransactions(byte[] transactionData) {
				try {
					ByteArrayInputStream bais = new ByteArrayInputStream(transactionData);
					DataInputStream dis = new DataInputStream(bais);
					int size = dis.readInt();
					ArrayList<String> output = new ArrayList<>(size);
					for (int i = 0; i < size; i++) {
						output.add(dis.readUTF());
					}
					dis.close();
					return output;
				}
				catch (IOException e) {
					log.error("transaction was in an invalid data format -rejecting", e);
					return null;
				}
			}

			@Override
			public List<String> deserializeState(DataInput dataInput) throws IOException {
				int count = dataInput.readInt();
				List<String> result = new ArrayList<>(count);
				for (int i = 0; i < count; i++) {
					result.add(dataInput.readUTF());
				}
				return result;
			}

			@Override
			public void serializeState(List<String> state, DataOutput dataOutput) throws IOException {
				dataOutput.writeInt(state.size());
				try {
					state.forEach(str -> {
						try {
							dataOutput.writeUTF(str);		
						}
						catch (IOException e) {
							throw new IllegalStateException(e);
						}
					});
				}
				catch (IllegalStateException e) {
					throw (IOException) e.getCause();
				}
			}
		};
	}
}
