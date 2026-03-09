import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.withSurfApiBukkit

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

group = "dev.slne.surf"

surfPaperPluginApi {
    mainClass("dev.slne.surf.enchantment.SurfEnchantment")
    bootstrapper("dev.slne.surf.enchantment.SurfEnchantmentBootstrap")
    generateLibraryLoader(false)
    foliaSupported(true)
    authors.addAll("Ammo", "twisti", "Jo_field")

    serverDependencies {
        registerRequired("CoreProtect")
    }

    runServer {
        withSurfApiBukkit()
    }
}
dependencies {
    compileOnly("net.coreprotect:coreprotect:22.3")
}
