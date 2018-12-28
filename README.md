# Lambdatest Jenkins Plugin

#### This Plugin is used to run automated selenium tests on LambdaTest Cloud.

### Base Configuration
https://github.com/jenkinsci/gradle-jpi-plugin

### Installation

##### via the interface
1. Go to your installation's management screen.
2. Click **Manage Plugins**.
3. Click the **Available** tab.
4. Find the **LambdaTest Automation Plugin** and select the checkbox.
5. then click either **Install without restart** or **Download now and install after restart**.

### Building the plugin for testing/development

<pre>gradle clean</pre>

###### This command will run the gradle Task :build
<pre>gradle build</pre>

###### This command will run the gradle Task :server
<pre>gradle server</pre>

###### This command will run the gradle JVM with specified httpPort.
<pre>gradle server -Djenkins.httpPort=8082</pre>

## Release Instructions

In a continuous integration environment like Jenkins or Hudson, you don't want to have an interactive release process. To avoid having to enter any information manually during the process, you can tell the plugin to automatically set and update the version number.

You can do this by setting the `release.useAutomaticVersion` property on the command line, or in Jenkins when you execute gradle. The version to release and the next version can be optionally defined using the properties `release.releaseVersion` and `release.newVersion`.

```bash
$ gradle release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=1.1
```

## License

This program is free software and is distributed under an [MPL-2.0 License](LICENSE).
