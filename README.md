This project is a simple example of [ml-camel-mlcp](https://github.com/rjrudin/ml-camel-mlcp), a [Camel](http://camel.apache.org/) component for invoking [MarkLogic Content Pump](https://docs.marklogic.com/guide/ingestion/content-pump). This project shows you how to setup a "hot folder" such that any time a file is copied into a certain directory, it is automatically ingested into MarkLogic via Camel and Content Pump. And you can reuse all Content Pump import arguments while doing so.

How do I use this?
----
Just do the following:

1. Clone this repository
2. Run "./gradlew mlDeploy camelRun"
  1. Make sure "gradlew" is executable
  2. If you're on Windows, run "gradlew mlDeploy camelRun" instead
  3. If you have Gradle available on your path already, just run "gradle". "gradlew" is included to minimize the setup necessary.

The first Gradle task - mlDeploy - uses [ml-gradle](https://github.com/rjrudin/ml-gradle) to create a new MarkLogic application with a REST API server on port 8310 (feel free to change the port, it's specified in the gradle.properties file). 

The second task - camelRun - fires up Camel. By default, Camel will watch a directory named "inbox/mlcp" within the directory where you cloned this repository. When you a copy/move a file into that directory, Camel will read it in and then pass it off to Content Pump, which will ingest it into MarkLogic. You'll see plenty of logging in the console window from which you ran the Gradle tasks. 

To check your results as you ingest files, either use qconsole or try [your application's REST API search endpoint](http://localhost:8310/v1/search). 

How can I customize this?
----
The (Camel config file)[https://github.com/rjrudin/ml-camel-client/blob/master/src/main/resources/META-INF/camel-routes.xml] defines all of the Camel routes, including the one mentioned above. You'll need a little bit of Camel knowledge, but basically, you can make any modifications you wish to this file and then just run "camelRun" again.

How can I use this in my existing application?
----
Easy - you'll notice there's no real source code in this project - there are just some small config files. The build.gradle file shows you how to declare a dependency on ml-camel-mclp. Once you've done that - and you can do that using Maven/Gradle/whatever - it's simply a matter of how you choose to configure Camel. This project uses a Camel XML file to specify routes and a Spring file to use Spring for running Camel - those config files are under src/main/resources. But it's up to you how you want to configure Camel, and it's best to consult the Camel docs for all the different options that you have. 
