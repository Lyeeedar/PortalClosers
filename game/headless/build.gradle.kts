plugins {
	kotlin("jvm")
}

sourceSets {
	main {
		java.srcDirs("src/", "../../engine/headless/src/")
		resources.srcDirs("../assets")
	}
}

dependencies {
	val gdxVersion: String by project
	val ktxVersion: String by project
	val junitVersion: String by project

	implementation(project(":core"))

	implementation(kotlin("stdlib"))

	implementation("com.badlogicgames.gdx:gdx-backend-headless:$gdxVersion")
	implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
	implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
	implementation("com.badlogicgames.gdx:gdx-tools:$gdxVersion")

	implementation("io.github.libktx:ktx-collections:$ktxVersion")

	implementation("org.mockito:mockito-all:1.10.19")

	implementation("com.auth0:java-jwt:3.9.0")
	implementation("com.google.api-client:google-api-client:1.30.7")
	implementation("com.google.apis:google-api-services-androidpublisher:v3-rev20191113-1.30.3")

	implementation("com.google.cloud:google-cloud-translate:1.94.2")
	implementation("org.languagetool:language-en:4.8")

	implementation("junit:junit:$junitVersion")

	implementation("net.java.dev.jna:jna:5.9.0")
	implementation("net.java.dev.jna:jna-platform:5.9.0")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.register<JavaExec>("compileResources") {
	main = "com.lyeeedar.headless.CompilerRunner"
	classpath = sourceSets.main.get().runtimeClasspath
	standardInput = System.`in`
	workingDir = file("../assets")
}

tasks.register<JavaExec>("testResources") {
	main = "com.lyeeedar.headless.AssetTester"
	classpath = sourceSets.main.get().runtimeClasspath
	standardInput = System.`in`
	workingDir = file("../assets")
}

tasks.register<JavaExec>("longTest") {
	main = "com.lyeeedar.headless.LongTest"
	classpath = sourceSets.main.get().runtimeClasspath
	standardInput = System.`in`
	workingDir = file("../assets")
}

val appId: String by project
tasks.register<JavaExec>("gameLoopTest") {
	main = "com.lyeeedar.headless.GameLoopTest"
	classpath = sourceSets.main.get().runtimeClasspath
	standardInput = System.`in`
	workingDir = file("../assets")
	args = listOf(appId)
}

tasks.register<JavaExec>("releaseAndroidToPlaystore") {
	main = "com.lyeeedar.headless.AndroidRelease"
	classpath = sourceSets.main.get().runtimeClasspath
	standardInput = System.`in`
	workingDir = file("../assets")
	args = listOf(appId)
}

tasks.register<JavaExec>("autoLocalise") {
	main = "com.lyeeedar.headless.AutoLocaliser"
	classpath = sourceSets.main.get().runtimeClasspath
	standardInput = System.`in`
	workingDir = file("../assets")
}

tasks.register<Jar>("compilerDist") {
	from(files(sourceSets.main.get().output.classesDirs))
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
		.exclude("META-INF/MANIFEST.MF", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
		.exclude("en-*.bin")
		.exclude("**/akka/**")
		.exclude("**/opennlp/**")
		.exclude("**/android/**")
		.exclude("**/carrotsearch/**")
		.exclude("**/fasterxml/**")
		.exclude("**/google/**")
		.exclude("**/apache/**")
		.exclude("**/bridj/**")
		.exclude("**/languagetool/**")
		.exclude("**/scala/**")

	destinationDir = file("$rootDir/game/caches")
	archiveFileName.set("compiler.jar")

	manifest {
		attributes["Main-Class"] = "com.lyeeedar.headless.CompilerRunner"
	}
}

tasks.compileKotlin {
	kotlinOptions.freeCompilerArgs += "-Xmulti-platform"
}