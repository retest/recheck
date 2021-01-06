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

* Since 01.12.2020 this project is owned and developed by [UBS Hainer GmbH](https://ubs-hainer.com/).

### Bug Fixes

* Fix a rare `java.nio.file.FileSystemAlreadyExistsException` when accessing filters concurrently.
* Fix 1-click-maintenance not respecting the key of the `AttributeDifference`. 

### New Features

* Metadata differences are now filterable via the recheck.ignore. New `volatile-metadata.filter` and `metadata.filter` filters have been added, where the first one filters metadata which is added for diagnosing purposes and too verbose to be displayed upon every change and the latter filters _all_ metadata. They can be imported with `import: volatile-metadata.filter` and `import: metadata.filter` in `recheck.ignore`.
* Add `exclude()` filter expression to allow child elements to be excluded. Ignoring an element `matcher: type=body, exclude(matcher: type=button)` will ignore everything below the `body` element but still capture changes for `button` elements.
* Add a `disableReportUpload` method.
* The `ProjectLayout` will now be automatically detected (Maven and Gradle) and will throw an error if it cannot fall back to Maven, in which case a custom `ProjectLayout` has to be provided.
* In case of an exception in the filter code, it will be treated as not filtered.

### Improvements

* Have the console output much more compact in terms of whitespace used. Con: makes it harder to spot difference details...
* Project discovery and initialization is now done within `RecheckOptionsBuilder#build` instead of `RecheckImpl#new` to allow earlier access to project variables (e.g. retest.properties). Projects (i.e. `.retest` folders) should still be detected correctly.


--------------------------------------------------------------------------------


[1.11.2] (2020-07-01)
---------------------

### Bug Fixes

* Fix accepting of attributes without warning deletes warnings of similar attribute differences.
* Fix NPE when applying an identifying attribute that has been inserted.


[1.11.1] (2020-06-10)
---------------------

### Bug Fixes

* Fix 1-click-maintenance not respecting the key of the `AttributeDifference`. 


[1.11.0] (2020-05-15)
---------------------

### Breaking Changes

* Removed the now obsolete `RandomSuffixRetestIdProvider` class.

### Bug Fixes

* Fix `java.lang.ClassCastException`: `de.retest.recheck.ui.diff.InsertedDeletedElementDifference` cannot be cast to `de.retest.recheck.ui.diff.IdentifyingAttributesDifference`.
* Fix similar differences between checks not being accepted together, bringing back 1-click-maintenance.

### New Features

* The `Highlighter` interface enables to define custom highlighting for certain keywords defined in the printers intended for the console output (e.g. using ***recheck.cli***).
* Add ignore rule that allows to ignore elements based on any of their attributes. E.g. for text it works like so: `matcher: text=Sign In`.

### Improvements

* Introduce property `de.retest.recheck.rehub.upload.attempts=3` to retry report uploads to ***rehub*** if they fail (e.g. due to unstable connections). This will reduce failing tests if the upload fails.


[1.10.2] (2020-03-16)
---------------------

### Bug Fixes

* Fix authentication not working because service URL has been changed to a more human readable format.


[1.10.1] (2020-03-10)
---------------------

### Bug Fixes

* Fix that using the import filter statement `import: ${name}` caused an infinite loop, although the filter specified had no cyclic dependencies.


[1.10.0] (2020-03-04)
---------------------

### Bug Fixes

* When defining an ignore rule with element and attribute, this rule is now applied to the whole _subtree_, i.e. all child elements of the specified element. We found that this is more intuitive and straightforward and more often what people would expect. Also, it is more in line with the current behavior when just ignoring an element (which also ignores all of its child elements).
* Fixed a bug where, when filtering for classes, the given classes had to contain _all_ of the elements classes, not the other way around. 

### New Features

* You can now specify that e.g. the pixel-diff filter should only apply to a specific subtree of the given elements, or to only a specific attribute. For more details, see the [docs](https://docs.retest.de).
* You can now specify that a specific value-regex should be filtered, either globally, for specific elements or specific attributes. This allows to e.g. ignore a date, but still ensure that it is actually a valid date.
* You can now specify to ignore a percentage color-diff, much like a pixel-diff. This allows to ignore, e.g. minor color changes or changes where opacity value is added or missing. Can be added globally, per element or attribute or combination thereof.
* Add the option to filter elements based on whether they are inserted, deleted or changed.
* The report output now displays the `retestId`: `input (input-retest-id) at 'html[1]/body[1]/input[1]'`. This may be used, for example, by a filter: `matcher: retestId=input-retest-id`.

### Improvements

* File names in log output should now always be in single quotes, making it easier to distinguish and select them.
* Printer layout is improved (in `MetadataDifferencePrinter` and `AttributeDifferencePrinter`). Differences will now be printed per line and aligned for easier comparison.
* Prefer the text over the ID in the default recheckId provider, because the ID is often generated, the text never is.


[1.9.0] (2020-01-29)
--------------------

### Breaking Changes

* Reports before version 1.9.0 cannot be loaded anymore. Simply re-run your tests with the new recheck version to create them again.

### Bug Fixes

* The project root is not searched for multiple times on startup of recheck.
* Add timeout in case it takes too long to detect the current Git branch.
* Don't print metadata prefix if the all given metadata differences are ignored.

### New Features

* The `ProjectLayout` may now retrieve the test source root for the current project in order to allow for test healing to be applied. This is required for custom `ProjectLayouts` to be implemented, in order for this feature to work. For more information, please refer to the documentation.
* Use `import: $reference` to import another filter into a filter or an ignore file. Ignoring all positioning changes is now easily possible with `import: positioning.filter`. Whole cascades and hierarchies of filters can now be created this way. 

### Improvements

* In case there is no project specific ignore, fall back to the recheck.ignore file in the home directory for applied changes.
* Remove compile level dependency on logback.
* When updating Golden Masters, now the name of the file is returned, not the name of the Golden Master.


[1.8.0] (2019-12-13)
--------------------

### Breaking Changes

* Reports before version 1.8.0 cannot be loaded anymore. Simply re-run your tests with the new recheck version to create them again.

### Bug Fixes

* Fix `.retest/retest.properties` not being read. You can now use this file to configure recheck and its extensions.

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
* Change the pixel-diff filter to use the unit of measure. Existing filters will be converted to `px`.


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
