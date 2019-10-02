Changelog
=========

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/) and this project adheres to
[Semantic Versioning](https://semver.org/).

But we group the changes as follows:

* *Breaking Changes* when a change requires manual activity at update.
* *Bug Fixes* when we fix broken functionality.
* *New Features* for added functionality.
* *Improvements* for other changed parts.


Table of Contents
-----------------

[TOC]


[Unreleased]
------------

### Breaking Changes

### Bug Fixes

* Do not parse the CSS attribute `box-shadow` for the filter `pixel-diff`.
* Update screenshots always instead of never.

### New Features

* Added some more `RetestIdProviders`:
    * `de.retest.recheck.ui.descriptors.idproviders.RandomSuffixRetestIdProvider`: Add a random suffix to the element.
    * `de.retest.recheck.ui.descriptors.idproviders.ElementCountingRetestIdProvider`: Add a counter suffix to the element.

### Improvements

* `RecheckImpl` can now be initialized as a member variable without additional parameters if inside a test class.


--------------------------------------------------------------------------------


[1.5.0] (2019-09-13)
--------------------

### Breaking Changes

* Improve the `RecheckOptions` and its `Builder`.

### Bug Fixes

* Fix a bug, where loading ignore files didn't work properly.
* Display inserted and deleted elements correctly.
* Fix a nasty bug, where the `retestId` sometimes changed upon persisting as XML.

### Improvements

* The persisted binary report now contains the version number of the recheck version that persisted it.
* The persisted binary report now is also zipped, reducing file size.
* On error during creating the persisted binary report file, delete the file to not leave corrupt files behind. 
* The recheck API key can now be accessed via `Rehub#getRecheckApiKey()`.
* Give better error message, if using rehub and no API key is specified.
* Move HTML attributes to the correct filters.
* Treat `*.filter.ignore.js` files as normal filter files.
* Cache calls to JavaScript filter to improve performance.
* Introduce new `NamingStrategy` and `ProjectLayout` to replace old `FileNamerStrategy`, but be downwards compatible for now.


[1.4.0] (2019-08-19)
--------------------

In previous releases changes were unlogged...
