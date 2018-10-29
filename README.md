# ![recheck logo](https://user-images.githubusercontent.com/1871610/41766965-b69d46a2-7608-11e8-97b4-c6b0f047d455.png)

[![Build Status](https://travis-ci.com/retest/recheck.svg?branch=master)](https://travis-ci.com/retest/recheck)
[![Latest recheck on Maven Central](https://maven-badges.herokuapp.com/maven-central/de.retest/recheck/badge.svg?style=flat)](https://mvnrepository.com/artifact/de.retest/recheck)
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


## Prerequisites

Currently available as a Java API with support for JUnit Vintage (v4), JUnit Jupiter (v5) and TestNG.

## Set up

You only need to download recheck directly if you plan to implement it for an additional interface. If you want to use an existing implementation (e.g. [recheck-web](https://github.com/retest/recheck-web/), see ["Features" section](#features)), you would rather reference this implementation, for instance via Maven, and have the transitive dependencies be automatically resolved.

Download recheck here on [GitHub](https://https://github.com/retest/recheck/releases) or add it as dependency through [Maven Central](https://search.maven.org/search?q=g:de.retest%20a:recheck):

* group ID: `de.retest`
* artifact ID: `recheck`
* version: [![Latest recheck on Maven Central](https://maven-badges.herokuapp.com/maven-central/de.retest/recheck/badge.svg?style=flat)](https://mvnrepository.com/artifact/de.retest/recheck)

## License

This project is licensed under the [AGPL license](LICENSE).
