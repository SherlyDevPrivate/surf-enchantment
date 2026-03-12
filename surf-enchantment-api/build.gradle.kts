import dev.slne.surf.surfapi.gradle.util.slneReleases

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

publishing {
    repositories {
        slneReleases()
    }
}