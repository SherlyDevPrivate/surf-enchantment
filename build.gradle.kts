import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf"

surfPaperPluginApi {
    mainClass("dev.slne.surf.enchantment.SurfEnchantment")
    bootstrapper("dev.slne.surf.enchantment.SurfEnchantmentBootstrap")
    generateLibraryLoader(false)
    authors.addAll("Ammo", "twisti")

    runServer {
        withSurfApiBukkit()
    }
}