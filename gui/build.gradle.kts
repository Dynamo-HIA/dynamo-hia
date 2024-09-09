/*
 * Gradle file for the Dynamo-HIA application swt GUI
 */


plugins {
    id("dynamo.hia.java-application-conventions")
    id("com.diffplug.eclipse.mavencentral")
    id("org.beryx.runtime") version "1.13.1"
}


apply(plugin = "com.diffplug.eclipse.mavencentral")

eclipseMavenCentral {
    release("4.30.0", {
        implementation("org.eclipse.jdt.core")
        implementation("org.eclipse.swt")
        implementation("org.eclipse.jface.databinding")
        implementation("org.eclipse.ui.ide")
        implementation("org.eclipse.core.resources")

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
    implementation(project(":application"))

    implementation("junit:junit:4.13.2")

    implementation("commons-logging:commons-logging:1.2")
    implementation("commons-configuration:commons-configuration:1.5")
    implementation("commons-collections:commons-collections:3.2.2")
    implementation("commons-lang:commons-lang:2.6")
    implementation("log4j:log4j:1.2.17")
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

application {

	if (org.gradle.internal.os.OperatingSystem.current().isMacOsX()) {
	    //this is an OSX-only arg needed to fix an issue with SWT
	    applicationDefaultJvmArgs = listOf("-XstartOnFirstThread")
	}
    
    // Define the main class for the application.
    mainClass.set("nl.rivm.emi.dynamo.ui.main.main.Main")
    applicationName.set("dynamo-hia")
}

tasks.test {
    //use Junit for tests
    useJUnitPlatform()
}
