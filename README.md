# ![recheck logo](https://user-images.githubusercontent.com/1871610/41766965-b69d46a2-7608-11e8-97b4-c6b0f047d455.png)

[![Build Status](https://travis-ci.com/retest/recheck.svg?branch=master)](https://travis-ci.com/retest/recheck)
[![license](https://img.shields.io/badge/license-AGPL-brightgreen.svg)](https://github.com/retest/recheck/blob/master/LICENSE)
[![PRs welcome](https://img.shields.io/badge/PRs-welcome-ff69b4.svg)](https://github.com/retest/recheck/issues?q=is%3Aissue+is%3Aopen+label%3A%22help+wanted%22)
[![code with hearth by retest](https://img.shields.io/badge/%3C%2F%3E%20with%20%E2%99%A5%20by-retest-C1D82F.svg)](https://retest.de/en/)

recheck is a general framework that allows replacing manual asserts and checking everything at once.


## Features

* Easy creation and maintenance of checks for [web](https://github.com/retest/recheck-web/) and [Java Swing](http://retest.org/) (more coming).
* Semantic comparison of contents.
* Easily ignore volatile elements, attributes or sections.
* One-click maintenance to update tests with intended changes.
* No unexpected changes go unnoticed.
* The Git for your interface.


### Prerequisites

Currently available as a Java API with support for JUnit Vintage (v4), JUnit Jupiter (v5) and TestNG.


### Installing

You only need to download recheck directly if you plan to implement it for an additional interface. If you want to use an existing implementation (e.g. [recheck-web](https://github.com/retest/recheck-web/), see ["Features" section](#features)), you would rather reference this implementation, for instance via Maven, and have the transitive dependencies be automatically resolved.

You can install recheck using the [latest release](https://github.com/retest/recheck/releases/) or by adding it as a Maven dependency in your POM:

```
<dependency>
	<groupId>de.retest</groupId>
	<artifactId>recheck</artifactId>
	<version>0.1.0</version>
</dependency>
```


## License

This project is licensed under the [AGPL license](LICENSE).


### Building

To build this project locally, you have to skip JAR signing.

For normal builds use:

```
mvn deploy -Dgpg.skip=true
```

For making releases use:

```
mvn release:prepare -Darguments="-Dgpg.skip=true"
```

