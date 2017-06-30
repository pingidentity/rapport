# [Rapport](https://en.wiktionary.org/wiki/rapport) Distributed Consensus API

The following project is Rapport, an API for participating in distributed consensus systems.

The application API is built as a single topic message queue - a peer in the system broadcasts messages, and then receives messages in consensus order.

The system focuses on processing those messages as functions which alter system state - as these messages arrive in consensus order, each participant
winds up having the same state.

An application can then spin up additional threads to interact with a platform object, exposing the current state and dispatching new messages to 
(eventually) modify state

## Implementations

There are two systems currently implementing the Rapport API:

- [**Swirlds**](http://swirds.com): Rapport was primarily written during the closed alpha period of the Swirlds platform, and the API is partially based on the Swirlds API.
- **Lonely**: The lonely implementation is a simple, non-distributed system built for testing. It simply echoes messages back that your application sends.

   _Note_: The Swirlds platform is not distributed as part of this system. Certain components are required from the distribution to build the Swirlds implementation. See more details [Here](./rapport-impl-swirds/README.md).

## Structure

The project has the following subproject structure:

- **rapport-api**: core API and SPI for implementations, as well as a launcher app to run applications (with compatible backend implementations)
- **rapport-impl-lonely**: a single, local peer implementation, primarily for testing purposes. Simply echoes events back as authoritative.
- **rapport-impl-swirlds**: a hashgraph-based implementation based on the Java Swirlds platform.
- **rapport-swirlds-converter**: The Swirlds platform does not allow rapport to invoke it via the platform runner, which requires configuration be specified using its configuration location and format. This tool converts a rapport configuration into the swirlds configuration files.
- **echo-application**: a simple echo application, building by default against the *lonely* backend.
