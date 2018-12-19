# Lambdatest Jenkins Plugin

### Base Configuration
https://github.com/jenkinsci/gradle-jpi-plugin

### Building the plugin for testing/development

<pre>gradle clean</pre>

This command will run the gradle Task :build
<pre>gradle build</pre>

This command will run the gradle Task :server
<pre>gradle server</pre>

This command will run the gradle JVM with specified httpPort.
<pre>gradle server -Djenkins.httpPort=8082</pre>

## License

This program is free software and is distributed under an [MPL-2.0 License](LICENSE).
