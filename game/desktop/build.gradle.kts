project.dependencies {
	// put project dependencies in here
}

tasks.register<Jar>("mapGenerationPreviewDist") {
	from(files(project.the<SourceSetContainer>()["main"].output.classesDirs))
	from(configurations["runtimeClasspath"].map { if (it.isDirectory) it else zipTree(it) })

	manifest {
		attributes["Main-Class"] = "com.lyeeedar.desktop.MapGenerationPreviewLauncher"
	}
}