#Dynamo-HIA

**Copyright (C) National Institute for Public Health and the Environment (RIVM)**
Dynamo-HIA is licenced under the EUROPEAN UNION PUBLIC LICENCE (EPL) v. 1.2

This repo contains the Dynamo-HIA Model

Folders and files in this repo:

DynamoDataSet:	dataset for use with Dynamo-HIA
README: This file
acdm: The acdm model underlying Dynamo-HIA
application: the main Dynamo-HIA application, including batch runner
gui: SWT gui for Dynamo-GIA
attic: old subprojects for Dynamo-HIA that need to be (re)moved at some point
buildSrc: source for gradle build
gradle: copy of grade (the builder)
gradlew: gradle wrapper script used to build the application
gradlew.bat: Windows version of script used to build the application
settings.gradle.kts: main build script
utilities: various supporting utilities


To run Dynamo-HIA from gradle, using the GUI:

$ ./gradlew gui:run

Or in batch mode:

$ ./gradlew application:run --args BATCH_FILE_LOCATION

To create a compiled version of dynamo-hia, including java and an executable, use:

./gradlew gui:runtime

The files can be found in gui/build/image

To create an installable package of dynamo-hia, using an OS specific installer, use:

./gradlew gui:jpackage

