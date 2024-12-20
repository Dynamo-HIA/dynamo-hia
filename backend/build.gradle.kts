/*
 * Gradle file for the Dynamo-HIA application.
 */


plugins {
    id("dynamo.hia.java-application-conventions")
    id("com.diffplug.eclipse.mavencentral")
    id("org.beryx.runtime") version "1.13.1"
}


apply(plugin = "com.diffplug.eclipse.mavencentral")

//the application ideally should not need any eclipse classes
eclipseMavenCentral {
    release("4.30.0", {
        implementation("org.eclipse.jdt.core")
        implementation("org.eclipse.core.resources")
        implementation("org.eclipse.core.databinding")
        implementation("org.eclipse.swt")
        implementation("org.eclipse.jface.databinding")
        implementation("org.eclipse.ui.ide")

        // specify this to add the native jars for this platform
        useNativesForRunningPlatform()

        // specify that all transitive dependencies should be from this release
        constrainTransitivesToThisRelease()
    }
    )
}


dependencies {
    implementation(project(":utilities"))
    implementation(project(":acdm"))

    implementation("junit:junit:4.13.2")

    implementation("commons-logging:commons-logging:1.2")
    implementation("commons-configuration:commons-configuration:1.5")
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("commons-lang:commons-lang:2.6")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation("gov.nist.math.jama:gov.nist.math.jama:1.1.1")
    
    implementation("com.ibm.icu:icu4j:3.6.1")
    implementation("org.jfree:jcommon:1.0.17")
    implementation("org.jfree:jfreechart:1.0.19")

    //jfreechart-swt drags in a somewhat random version of swt, so we exclude it here.
    //swt etc are imported using the eclipseMavenCentral plugin
    
    implementation("org.jfree:jfreechart-swt:1.0") {
		exclude(group = "org.eclipse.swt", module="org.eclipse.swt.cocoa.macosx.x86_64")
    }
}


runtime {
	jpackage {
		imageName = "DYNAMO-HIA-backend"
		installerName = "DYNAMO-HIA-backend-installer"
                //options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages", "--strip-native-commands"))
                //modules.set(listOf("java.desktop", "java.management", "java.xml", "java.logging", "java.sql", "java.naming", "java.datatransfer"))

	}
}

application {

	if (org.gradle.internal.os.OperatingSystem.current().isMacOsX()) {
	    //this is an OSX-only arg needed to fix an issue with SWT
	    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
	}
    
     // Define the main class for the application (in this case the batch runner)
     mainClass.set("nl.rivm.emi.dynamo.batch.Runner")
     
     //set the application name, also for the executable and such
     applicationName = "DYNAMO-HIA-backend"
}

tasks.test {
    //use Junit for tests
    useJUnitPlatform()
}
