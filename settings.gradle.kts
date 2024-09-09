/*
 * Main settings file for dynamo-hia
 * Mostly defines the subprojects
 * For generic settings (such as the Java used) see the buildSrc folder, and the conventions defined there
 *
 */

plugins {
    // Apply the foojay-resolver plugin to allow automatic download of JDKs
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
   
    // Apply the settings fix for the goomp plugin https://github.com/diffplug/goomph/issues/203
    id("com.diffplug.configuration-cache-for-platform-specific-build") version "3.44.0"
}

rootProject.name = "dynamo-hia"
include("acdm", "utilities", "backend", "frontend")
