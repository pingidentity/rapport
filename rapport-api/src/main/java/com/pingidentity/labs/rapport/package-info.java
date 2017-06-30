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

/**
 * Main API for implementing a Rapport {@link com.pingidentity.labs.rapport.Application}.
 * 
 * An {@link com.pingidentity.labs.rapport.Application} consists of its interactor, which spins up a UI
 * or network interface for interacting with the distributed system around the backend's 
 * {@link com.pingidentity.labs.rapport.Coordinator}, and a {@link com.pingidentity.labs.rapport.StateManager}
 * which understands {@link com.pingidentity.labs.rapport.TransactionMessage}, sent by all 
 * {@link com.pingidentity.labs.rapport.Peer} - including ourselves. The state manager creates a 
 * representation of the state of the system for the interactor to expose, and for the interactor to send
 * transactions to manipulate the state.
 */
package com.pingidentity.labs.rapport;
