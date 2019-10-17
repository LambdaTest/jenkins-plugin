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

## License

This program is free software and is distributed under an [MPL-2.0 License](LICENSE).

## About LambdaTest
[LambdaTest](https://www.lambdatest.com/) is a cloud based selenium grid infrastructure that can help you run automated cross browser compatibility tests on 2000+ different browser and operating system environments. All test data generated during testing including Selenium command logs, screenshots generated in testing, video logs, selenium logs, network logs, console logs, and metadata logs can be extracted using [LambdaTest automation APIs](https://www.lambdatest.com/support/docs/api-doc/). This data can then be used for creating custom reports.
