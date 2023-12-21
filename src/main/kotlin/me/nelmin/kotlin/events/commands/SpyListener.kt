package me.nelmin.kotlin.events.commands

import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class SpyListener : Listener {
    private val instance = SpigotUltimate.instance

    @EventHandler
    fun onPlayerCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        instance.spyPlayers[event.player.uniqueId]?.forEach { uuid ->
            Bukkit.getPlayer(uuid).also { player ->
                if (player != null && player.isOnline) player.sendMessage(
                    TextBuilder(instance.configManager.config.get("format", "spy").asString())
                        .spyFormat(
                            event.player,
                            event.message.split(" ")[0]
                        ).getColorized()
                )
            }
        }
    }

}