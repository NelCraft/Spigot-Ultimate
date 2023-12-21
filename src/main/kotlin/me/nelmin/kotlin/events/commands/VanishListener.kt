package me.nelmin.kotlin.events.commands

import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class VanishListener : Listener {
    private val instance = SpigotUltimate.instance

    private val seePerm = instance.configManager.commands.get("vanish", "see_permission").asString()
    private val staffPerm = instance.configManager.config.get("staff_permission").asString()

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val deactivated = TextBuilder(
            instance.configManager.commands.get("vanish", "messages", "deactivated").asString()
        ).withPlaceholders(event.player).pluginPrefix().getColorized()

        if (instance.vanishedPlayers.contains(event.player)) {
            instance.server.onlinePlayers.forEach {
                if ((it.hasPermission(staffPerm) || it.hasPermission(seePerm)) && it != event.player)
                    it.sendMessage(deactivated)

                it.showPlayer(instance, event.player)
            }

            instance.vanishedPlayers.remove(event.player)
            event.player.sendMessage(deactivated)
        }
    }
}