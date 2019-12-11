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

* Reports before version 1.8.0 cannot be loaded anymore. Simply re-run your tests with the new recheck version to create them again.

### Bug Fixes

### New Features

* Ignore filters can now be set globally by creating a '.retest' folder with a 'recheck.ignore' file in the user home directory.
* Retrieve and handle metadata from the recheck adapter.
* Retrieve metadata for the created Golden Master or SUT state that contains the following:
    * Operating system name and version.
    * Machine name (if available).
    * Date and time.
    * Git branch and commit.
* Display specific metadata will after test execution when printing the differences. This includes the following:
    * Operating system name and version.

### Improvements

* Switched the underlying JavaScript engine (used for, e.g., `recheck.ignore.js`) by replacing Nashorn (deprecated) with [Mozilla's Rhino](https://github.com/mozilla/rhino/).
* Added 'target', 'rel' and 'xmlns' as attributes to invisible filters and 'scale' as attribute to style filter.
* Support XPath in the ignore file also directly like it's returned from Chrome (e.g. `/html/body/div[3]`).
* Changed the definition of the ignore rules from `matcher: id=title, attribute: font` to `matcher: id=title, attribute=font` and from `matcher: id=title, attribute-regex: font-.*` to `matcher: id=title, attribute-regex=font-.*` to be more consistent. recheck will automatically migrate existing `recheck.ignore` files.

--------------------------------------------------------------------------------


[1.7.0] (2019-11-21)
--------------------

### Breaking Changes

* Reports before version 1.7.0 cannot be loaded anymore. Simply re-run your tests with the new recheck version to create them again.

### Bug Fixes

* Fixed that JavaScript filters were not executed again after an error has been thrown.

### Improvements

* Change the default element identification mechanism within the `recheck.ignore` file (i.e. when using CLI or GUI) from XPath to retestId.
* Display suite description for test report printing so that tests now can properly be identified with their parent suite.
* Improved log messages of faulty JavaScript filters to show the file where the error actually happened.


--------------------------------------------------------------------------------


[1.6.0] (2019-11-06)
--------------------

### Bug Fixes

* Do not parse the CSS attribute `box-shadow` for the filter `pixel-diff`.
* Update screenshots always instead of never.
* Matching elements now properly ignores child elements for identifying attributes other than XPath.

### New Features

* Add some more `RetestIdProviders`:
    * `de.retest.recheck.ui.descriptors.idproviders.RandomSuffixRetestIdProvider`: Add a random suffix to the element.
    * `de.retest.recheck.ui.descriptors.idproviders.ElementCountingRetestIdProvider`: Add a counter suffix to the element.
* Add new element matcher for `class` attribute, which allows elements to be ignored by `matcher: class=some-class`.

### Improvements

* `RecheckImpl` can now be initialized as a member variable without additional parameters if inside a test class.
* The screenshot does now have a fixed filename. This makes it much easier to work with VCS.
* Add sensible default values to the recheck.ignore file that is installed. This will only be created with new project.


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
