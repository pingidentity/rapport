# File Format

A common file format known as a `constitution file` is available to configure the various backends. It is a JSON file containing a root object with the following keys:

## Application Class Name

The `applicationClass` key is a string giving the full Java class name of the `Application` interface implementation being run.

## Constitution

The `constitution` key contains a value, list or object to specify the initial state of the system. This typically includes the initial rules and settings for for how the system should work.

If the constitution is not common across all peers, the system itself will likely eventually fail with inconsistent state.

Note that systems are allowed (through voting or other system-defined mechanisms) to change their rules after the initial constitution. This should not change the constitution data provided here - instead, the state of the distributed system will override the initial constitution.

## Peers

The `peers` section defines the list of available starting peers in the system. The requirements of peers are specified by the backend - the lonely backend requires this section to be an empty array, while the Swirlds backend currently requires every peer to be specified, and for them to be specified in the same order across all peer installations.

The format of the individual peers is a JSON object defined by the backend. They may include information on how to connect to the peers over the internet.  However, the common field `nickname` is required to provide a name for local debugging information about peers, without resorting to printing out cryptographic thumbprints or the like.

Another field common in convention is the `local` flag, to indicate (if necessary) which peer is the local peer.

## Local Configuration

The `localConfiguration` section is a JSON value, object or list defined solely by the application. This is used to control local behavior, independent of the state of the distributed system. This is the recommended means to define things like network connections and logging behaviors.

Some backends support multiple local peers running in-process. This is not supported via this configuration format at this time.

## Other keys

The behavior of the file is such that any key not defined here which is presented at the root level will be ignored, and will not be exposed to the application. For that reason, There are several keys that might be used for administrative purposes. Examples would be:

- `version` - semantic version string of this file
- `applicationVersion` - semantic version string corresponding to the expected application, for documentation purposes
- `maintainer` - maintainer of the configuration file
- `revisionHistory` - array of objects describing revisions to this file, newest on top.
- `comment` - description of the file/peer network

Do note that if you add any of these metadata bits, a JSON string cannot contain newline characters.
