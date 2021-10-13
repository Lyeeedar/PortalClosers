plugins {
	kotlin("jvm")
}

sourceSets {
	main {
		java.srcDirs("src/", "../../engine/desktop/src/")
		resources.srcDirs("../assets")
	}
}

dependencies {
	val gdxVersion: String by project

	implementation(project(":core"))

	implementation(kotlin("stdlib"))

	implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:$gdxVersion")
	implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
	implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.register<Jar>("mapGenerationPreviewDist") {
	from(files(sourceSets.main.get().output.classesDirs))
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

	destinationDir = file("$rootDir/game/caches")
	archiveFileName.set("mapViewer.jar")

	manifest {
		attributes["Main-Class"] = "com.lyeeedar.desktop.MapGenerationPreviewLauncher"
	}
}

tasks.register<Jar>("dist") {
	from(files(sourceSets.main.get().output.classesDirs))
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
	from(file("../assets"))

	manifest {
		attributes["Main-Class"] = "com.lyeeedar.desktop.DesktopLauncher"
	}
}

tasks.register<Jar>("particlePreviewDist") {
	from(files(sourceSets.main.get().output.classesDirs))
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

	destinationDir = file("$rootDir/game/caches")
	archiveFileName.set("particleViewer.jar")

	manifest {
		attributes["Main-Class"] = "com.lyeeedar.desktop.ParticlePreviewLauncher"
	}
}

tasks.register<JavaExec>("run") {
	main = "com.lyeeedar.desktop.DesktopLauncher"
	classpath = sourceSets.main.get().runtimeClasspath
	standardInput = System.`in`
	workingDir = file("../assets")
	isIgnoreExitValue = true

	if ("mac" in System.getProperty("os.name").toLowerCase()) {
		jvmArgs("-XstartOnFirstThread")
	}
}

tasks.register<Jar>("abilityPreviewDist") {
	from(files(sourceSets.main.get().output.classesDirs))
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

	destinationDir = file("$rootDir/game/caches")
	archiveFileName.set("abilityViewer.jar")

	manifest {
		attributes["Main-Class"] = "com.lyeeedar.desktop.AbilityPreviewLauncher"
	}
}