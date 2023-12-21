package me.nelmin.kotlin.events

import io.papermc.paper.event.player.AsyncChatEvent
import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class AsyncChatEvent : Listener {
    private val instance = SpigotUltimate.instance

    @EventHandler
    fun onAsyncChat(event: AsyncChatEvent) {
        event.isCancelled = true
        val message = LegacyComponentSerializer.legacy('&').serialize(event.message())

        if (message == "##su") {
            event.player.sendMessage(
                TextBuilder(
                    "This Server uses Spigot Ultimate v${instance.pluginMeta.version} made by NelCraft "
                ).pluginPrefix().getColorized()
            )
            return
        }
        if (message.contains("##su")) return

        instance.server.broadcast(
            TextBuilder(
                instance.configManager.config.get("format", "chat").asString()
            )
                .replace("%message%", message)
                .withPlaceholders(event.player).getColorized()
        )
    }

}