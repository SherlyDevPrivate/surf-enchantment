import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin") version "1.21.11+"
}

group = "dev.slne.surf"

surfPaperPluginApi {
    mainClass("dev.slne.surf.enchantment.SurfEnchantment")
    bootstrapper("dev.slne.surf.enchantment.SurfEnchantmentBootstrap")
    generateLibraryLoader(false)
    foliaSupported(true)
    authors.addAll("Ammo", "twisti", "red", "jofield")

    runServer {
        withSurfApiBukkit()
    }
}