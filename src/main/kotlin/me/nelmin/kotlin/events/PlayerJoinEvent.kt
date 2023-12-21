package me.nelmin.kotlin.events

import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinEvent : Listener {
    private val instance = SpigotUltimate.instance

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {}
}