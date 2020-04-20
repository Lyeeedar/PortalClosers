
buildscript {
	extra["appVersionCode"] = 1
	extra["appVersion"] = "0.0.1"
	extra["applicationId"] = "com.lyeeedar.portalclosers"

	rootProject.apply(from = rootProject.file("engine/versions.gradle.kts"))

	repositories {
		gradlePluginPortal()
		google()
		mavenCentral()
		jcenter()
		maven("https://oss.sonatype.org/content/repositories/snapshots/")
		maven("https://maven.fabric.io/public")
	}

	dependencies {
		classpath("com.android.tools.build:gradle:" + extra["androidToolsVersion"] as String)

		classpath(kotlin("gradle-plugin", extra["kotlinVersion"] as String))

		classpath("com.lyeeedar.gradle-plugins:gradle-plugins")

		classpath("com.google.gms:google-services:" + extra["googleServicesVersion"] as String)
		classpath("io.fabric.tools:gradle:" + extra["fabricToolsVersion"] as String)
		classpath("com.google.firebase:perf-plugin:" + extra["firebasePerfPluginVersion"] as String)
	}
}

plugins {
	base
}

allprojects {
	repositories {
		jcenter()
		google()
		mavenCentral()
		maven("https://oss.sonatype.org/content/repositories/snapshots/")
		maven("https://oss.sonatype.org/content/repositories/releases/")
	}
}