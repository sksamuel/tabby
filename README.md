# tabby 

[![Build Status](https://github.com/sksamuel/tabby/workflows/master/badge.svg)](https://github.com/sksamuel/tabby/actions) 
[<img src="https://img.shields.io/maven-central/v/com.sksamuel.tabby/tabby.svg?label=latest%20release"/>](https://central.sonatype.com/search?q=tabby) 
![GitHub](https://img.shields.io/github/license/sksamuel/tabby) 
[<img src="https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fcentral.sonatype.com%2Frepository%2Fmaven-snapshots%2Fcom%2Fsksamuel%2Ftabby%2Ftabby%2Fmaven-metadata.xml&strategy=highestVersion&label=maven-snapshot">](https://central.sonatype.com/repository/maven-snapshots/com/sksamuel/tabby/tabby/maven-metadata.xml)

Just some helpers for extending Kotlin's functional programming support.

### Versions

* Use 2.0.x for Kotlin 1.6.x
* Use 2.2.x for Kotlin 1.7.x
* Use 3.0.x for Kotlin 2.2+

### Feature complete

This project is considered feature complete and 3.0.1 is the last release (unless bugs that warrant a release are found).

The primary focus of this project was enhancing the `Result` class to offer better functional error handling - by adding many of the common methods you would normally see on a `Try` monad. There is only a finite number of methods to add and so the feature set has been stable since 2023. 

In addition, the inclusion of Rich Errors in Kotlin 2.4 will mean error handling via `Result` will largely become obsolete in favour of the new approach.
