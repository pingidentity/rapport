# Swirlds Backend Implementation for Rapport

The following directory houses the bridge between the Rapport API for applications and the Swirlds platform.

## Building

The Swirlds platform is not distributed within this project. It must be fetched separately from [the Swirlds Developer Resource Download Page](http://www.swirlds.com/download/). Once downloaded, you can place `swirlds.jar` within the lib directory and build using gradle.

## Running

The Swirlds platform has its own framework for running applications, and unfortunately currently does not expose API for third parties to launch it, instead only supporting launching via a command line. For this reason, your build target needs to be integrated into the Swirlds platform in order to run it.

1. Build your application and this swirlds backend
1. Drop your application and its dependencies into the swirlds platform `data/lib` directory
1. Drop this build target (rapport-impl-swirlds.jar) into the swirlds platform `data/apps` directory
1. Drop dependencies for this build target (rapport-api.jar) into the swirlds platform `data/apps` directory
1. Run the `rapport-swirlds-converter` on your constitution.json to convert to the needed config.txt and crypto key stores
1. Generate a launch script to start swirlds, and drop it into the root of the platform.