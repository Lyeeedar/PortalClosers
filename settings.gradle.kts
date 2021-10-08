rootProject.name = "PortalClosers"

include(":core")
project(":core").projectDir = File("game/core")

include(":android")
project(":android").projectDir = File("game/android")

include(":desktop")
project(":desktop").projectDir = File("game/desktop")

include(":headless")
project(":headless").projectDir = File("game/headless")

includeBuild("engine/gradle-plugins")