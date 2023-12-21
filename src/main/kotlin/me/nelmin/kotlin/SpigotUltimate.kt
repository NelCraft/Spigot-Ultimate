package me.nelmin.kotlin

import lombok.Getter
import me.nelmin.kotlin.command.team.FlyCommand
import me.nelmin.kotlin.command.team.HealCommand
import me.nelmin.kotlin.command.team.SpyCommand
import me.nelmin.kotlin.command.team.VanishCommand
import me.nelmin.kotlin.config.ConfigManager
import me.nelmin.kotlin.events.AsyncChatEvent
import me.nelmin.kotlin.events.PlayerJoinEvent
import me.nelmin.kotlin.events.commands.SpyListener
import me.nelmin.kotlin.events.commands.VanishListener
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.UUID

class SpigotUltimate : JavaPlugin() {

    companion object {
        lateinit var instance: SpigotUltimate
    }

    @Getter
    lateinit var pluginManager: PluginManager

    @Getter
    lateinit var configManager: ConfigManager

    @Getter
    val folder = "plugins/Spigot-Ultimate/"

    @Getter
    val vanishedPlayers: MutableSet<Player> = mutableSetOf()

    @Getter
    val oneHitBreakPlayers: MutableSet<Player> = mutableSetOf()

    // Map -> Target, Spy's
    @Getter
    val spyPlayers: MutableMap<UUID, MutableSet<UUID>> = mutableMapOf()

    override fun onEnable() {
        instance = this

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.severe("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (!File("plugins/PlaceholderAPI/expansions/Expansion-player.jar").exists()) {
            logger.warning("PlaceholderAPI Player Expansion not found. Please download it using:")
            logger.warning("/papi ecloud download player")
            logger.warning("/papi reload")
        }

        File(folder).also { if (!it.exists()) it.mkdirs() }

        val hasToStop = !File(folder, "config.yml").exists() || !File(folder, "commands.yml").exists()
        val hasToStopError = "Spigot-Ultimate is disabling itself because not all config files exist. Please restart this plugin or this server."

        this.pluginManager = this.server.pluginManager

        this.configManager = ConfigManager()
        val commands = this.configManager.commands

        if (!hasToStop) {
            if (commands.get("fly", "enabled").asBoolean()) {
                getCommand("fly")!!.setExecutor(FlyCommand())
                getCommand("fly")!!.tabCompleter = FlyCommand()
                this.pluginManager.registerEvents(VanishListener(), this)
            }
            if (commands.get("heal", "enabled").asBoolean()) {
                getCommand("heal")!!.setExecutor(HealCommand())
                getCommand("heal")!!.tabCompleter = HealCommand()
            }
            if (commands.get("spy", "enabled").asBoolean()) {
                getCommand("spy")!!.setExecutor(SpyCommand())
                getCommand("spy")!!.tabCompleter = SpyCommand()
                this.pluginManager.registerEvents(SpyListener(), this)
            }
            if (commands.get("vanish", "enabled").asBoolean()) {
                getCommand("vanish")!!.setExecutor(VanishCommand())
                getCommand("vanish")!!.tabCompleter = VanishCommand()
            }

            this.pluginManager.registerEvents(PlayerJoinEvent(), this)
            this.pluginManager.registerEvents(AsyncChatEvent(), this)

            logger.info("Spigot-Ultimate is now successfully enabled!")
        } else {
            logger.warning(hasToStopError)
            this.pluginManager.disablePlugin(this)
        }
    }

    override fun onDisable() {}
}