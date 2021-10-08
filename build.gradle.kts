
buildscript {
	val androidPluginVersion: String by project
	val kotlinVersion: String by project

	val googleServicesVersion: String by project
	val fabricToolsVersion: String by project
	val firebasePerfPluginVersion: String by project

	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		google()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:$androidPluginVersion")
		classpath(kotlin("gradle-plugin", kotlinVersion))

		classpath("com.lyeeedar.gradle-plugins:gradle-plugins")

		classpath("com.google.gms:google-services:$googleServicesVersion")
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
		maven("https://maven.google.com")
	}
}

tasks.clean {
	delete(buildDir)
}