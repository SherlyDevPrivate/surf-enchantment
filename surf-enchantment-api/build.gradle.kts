plugins {
    id("dev.slne.surf.surfapi.gradle.paper-raw")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}