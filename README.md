# Kafka - der Prozess (WORK IN PROGRESS)

"Kafka der Prozess" (KdP for short) will be a business activity monitoring (BAM) tool. 
This means that it tracks activities of processes, which have been uploaded into the main engine of KdP.

The main reasons why you should implement KdP and maybe not use other BAM tools is a combination of:
* it is open source and free for everyone
* it is rather light-weight and therefore easy to comprehend (it avoids complex things like CEP ("complex event processing"))
* it should be able to keep up with firehoses of Kafka topics and other heavy workloads (i.e. it is designed to scale linearly by using scalable libraries and tools, only)
* it has very simple open interfaces to store and process events. Databases and event sources already available in your environment can be integrated easily.

## Architecture

### Module Organization
To be able to configure all have been divided into modules which can be combined to taylor KdP to your own needs:

* `engine` contains to the core engine parts
* `storage` contains modules which can be used to store processes and events as well as business data
* `event-watchers` contains modules which will catch all the events which will then be transferred to the engine
* `ui` contains the ui project to display the search ui and all the other fancy things which can be used by end users to interact with the system

## Development and Operations
### Building and running
After having cloned the repository, `mvn clean install` should build the application and each of its modules on your local machine.

Builds are performed by travis CI automatically and should provide the latest state of building. 

### Bug reports and contributing
You're always welcome to contribute bug reports (use the issues tab), tests, documentation and code!

### Professional support
Professional support can be obtained from some of our contributors for development, architecture consulting, installation, process modelling, training or operations. 
We will be pleased, if you take your chance to contact us now for more information on how we can support you.