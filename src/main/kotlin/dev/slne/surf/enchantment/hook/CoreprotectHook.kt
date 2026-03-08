import net.coreprotect.CoreProtect
import net.coreprotect.CoreProtectAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material

object CoreProtectHook {

    private val api: CoreProtectAPI? by lazy {
        val plugin = Bukkit.getServer().pluginManager.getPlugin("CoreProtect")

        if (plugin is CoreProtect) {
            val cpApi = plugin.api
            if (cpApi.isEnabled && cpApi.APIVersion() >= 9) {
                return@lazy cpApi
            }
        }
        null
    }

    fun logRemoval(user: String, location: Location, type: Material) {
        api?.logRemoval(user, location, type, null)
    }

    fun logPlacement(user: String, location: Location, type: Material) {
        api?.logPlacement(user, location, type, null)
    }
}