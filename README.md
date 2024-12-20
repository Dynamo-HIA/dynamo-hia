# Dynamo-HIA

This repo contains the Dynamo-HIA Model

**Copyright (C) National Institute for Public Health and the Environment (RIVM)**

Dynamo-HIA is licenced under the [EUROPEAN UNION PUBLIC LICENCE (EPL) v. 1.2](LICENCE.md)

## Folders and files in this repo


- DynamoDataSet:	dataset for use with Dynamo-HIA
- acdm: The rule-based acdm model underlying Dynamo-HIA
- attic: old subprojects for Dynamo-HIA that need to be (re)moved at some point
- backend: the main Dynamo-HIA application, including batch runner
- buildSrc: source for gradle build
- docs: documentation
- frontend: SWT gui for Dynamo-GIA
- gradle: copy of grade (the builder)
- utilities: various supporting utilities
- ECLIPSE.md: small howto on developing Dynamo-HIA using the Eclipse IDE
- LICENSE.md: the License for Dynamo-HIA
- README.md: this file
- gradlew: Linux and Mac-OS version of gradle wrapper script used to build the application
- gradlew.bat: Windows version of script used to build the application
- settings.gradle.kts: main build script

## Building and running Dynamo-HIA

Dynamo-HIA is built using Gradle. This is supported by most IDEs.

### Command line

To run Dynamo-HIA from gradle, using the GUI:

`$ ./gradlew frontend:run`

Or in batch mode:

`$ ./gradlew backend:run --args BATCH_FILE_LOCATION`

To create a compiled version of dynamo-hia, including java and an executable, use:

`$ ./gradlew frontend:runtime`

The files can be found in frontend/build/image

To create an installable package of dynamo-hia, using an OS specific installer, use:

`$ ./gradlew jpackage`

Gradle itself needs to be updated from time to time, mostly to support the latest Java.

NOTE: run this command twice for the update to all files to be completed

`$ ./gradlew wrapper --gradle-version latest`

### Eclipse

To start developing Dynamo-HIA using the Eclipse IDE see [the small howto](ECLIPSE.md).
