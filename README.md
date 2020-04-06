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

###### Build the Jenkins plugin file (will currently end in ".hpi".)
```bash
$ gradle jpi
```

###### This command will run the gradle JVM with specified httpPort.
<pre>gradle server -Djenkins.httpPort=8082</pre>

## Release Instructions

For new `releases`, bump `version` field in `build.gradle`, then execute the following command

```bash
$ gradle release -Prelease.useAutomaticVersion=true
```

## License

This program is free software and is distributed under an [MPL-2.0 License](LICENSE).

## About LambdaTest
[LambdaTest](https://www.lambdatest.com/) is a cloud based selenium grid infrastructure that can help you run automated cross browser compatibility tests on 2000+ different browser and operating system environments. LambdaTest supports all programming languages and frameworks that are supported with Selenium, and have easy integrations with all popular CI/CD platforms. It's a perfect solution to bring your [selenium automation testing](https://www.lambdatest.com/selenium-automation) to cloud based infrastructure that not only helps you increase your test coverage over multiple desktop and mobile browsers, but also allows you to cut down your test execution time by running tests on parallel.
