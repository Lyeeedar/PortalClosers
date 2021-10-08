plugins {
	kotlin("jvm")
	id("com.lyeeedar.gradle-plugins.rewriteSources")
}

dependencies {
	val gdxVersion: String by project
	val ktxVersion: String by project
	val squidlibVersion: String by project
	val kryoVersion: String by project
	val junitVersion: String by project
	val spineVersion: String by project
	val kotlinCoroutinesVersion: String by project

	implementation(kotlin("stdlib"))

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")

	implementation("com.badlogicgames.gdx:gdx:$gdxVersion")
	implementation("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")

	implementation("com.esotericsoftware:kryo:$kryoVersion")

	implementation("io.github.libktx:ktx-actors:$ktxVersion")
	implementation("io.github.libktx:ktx-collections:$ktxVersion")
	implementation("io.github.libktx:ktx-math:$ktxVersion")
	implementation("io.github.libktx:ktx-scene2d:$ktxVersion")

	implementation("com.squidpony:squidlib-util:$squidlibVersion")

	implementation("com.esotericsoftware.spine:spine-libgdx:$spineVersion")

	testImplementation("junit:junit:$junitVersion")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

sourceSets {
	main {
		java.srcDirs("src/", "../../engine/core/src/")
	}
	test {
		java.srcDirs("test/", "../../engine/core/test/")
	}
}

tasks.rewriteSources {
	inputDirs = sourceSets.main.get().java.srcDirs
	srcDirs = sourceSets.main.get().java.srcDirs
}
tasks.compileKotlin {
	dependsOn(tasks.rewriteSources)
	kotlinOptions.freeCompilerArgs += "-Xmulti-platform"
}