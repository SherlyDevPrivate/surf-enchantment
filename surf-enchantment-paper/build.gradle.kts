plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

dependencies {
    api(projects.surfEnchantmentApi)
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.enchantment.paper.SurfEnchantment")
    bootstrapper("dev.slne.surf.enchantment.paper.SurfEnchantmentBootstrap")

    generateLibraryLoader(false)
    foliaSupported(true)

    authors.addAll("Ammo", "twisti", "red", "jofield")
}