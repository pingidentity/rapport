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

/** For the preferred transaction format in the system, handle serialization and deserialization of supported
 * transactions into byte arrays.
 * 
 * A {@link StateSerializer} deals with converting state to and from a canonical representation. A
 * {@link TransactionSerializer} deals with translating the transactions to and from canonical binary
 * representations from the data received and sent by the back-end.
 * 
 * @param <T> Java type of the transactional message (or root type of several different messages) to be sent
 * by the coordinator and parsed/received by the {@link StateManager}
 */
public interface TransactionSerializer<T> {
	
	/**
	 * Serialize a list of transactions into a binary block. 
	 * 
	 * The messages sent by the backend are allowed to contain a block of transactions, and this method
	 * can attempt to optimize the format of such a block.
	 * 
	 * This method should only be called by a backend to serialize locally created transactions.
	 * 
	 * @param transactions ordered list of transactions to serialize.
	 * @return binary data for the backend to send
	 */
	byte[] serializeTransactions(List<? extends T> transactions);
	
	/**
	 * Deserialize a binary block into an ordered list of transactions
	 * 
	 * @param transactionData binary block containing one or more transactions
	 * @return ordered list of transactions within the block
	 */
	List<T> parseTransactions(byte[] transactionData);
}
