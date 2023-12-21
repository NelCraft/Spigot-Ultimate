package me.nelmin.kotlin.command.team

import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class FlyCommand : CommandExecutor, TabCompleter {
    private val instance = SpigotUltimate.instance
    private val cmds = instance.configManager.commands

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextBuilder(cmds.get("fly", "no", "player").asString()).pluginPrefix().getColorized())
            return true
        }

        if (!sender.hasPermission(cmds.get("fly", "permission").asString())) {
            sender.sendMessage(TextBuilder(cmds.get("fly", "no", "permission").asString()).pluginPrefix().getColorized())
            return true
        }

        val target: Player? = if (args.isNullOrEmpty()) sender else instance.server.getPlayer(args[1])

        if (target == null || !target.isOnline) {
            sender.sendMessage(TextBuilder(instance.configManager.config.get("player_not_found").asString()).pluginPrefix().getColorized())
            return true
        }

        if (target.gameMode == GameMode.CREATIVE) {
            val flyActivated = TextBuilder(
                cmds.get("fly", "messages", "activated").asString()
            ).replace("%player_name%", target.name).pluginPrefix().getColorized()

            sender.sendMessage(flyActivated)
            return true
        }

        if (target.allowFlight) {
            val flyDeactivated = TextBuilder(
                cmds.get("fly", "messages", "deactivated").asString()
            ).replace("%player_name%", target.name).pluginPrefix().getColorized()

            target.allowFlight = false
            target.sendMessage(flyDeactivated)
            instance.server.onlinePlayers.forEach {
                if (it.hasPermission(instance.configManager.config.get("staff_permission").asString()) && it != target)
                    it.sendMessage(flyDeactivated)
            }
        } else {
            val flyActivated = TextBuilder(
                cmds.get("fly", "messages", "activated").asString()
            ).replace("%player_name%", target.name).pluginPrefix().getColorized()

            target.allowFlight = true
            target.sendMessage(flyActivated)
            instance.server.onlinePlayers.forEach {
                if (it.hasPermission(instance.configManager.config.get("staff_permission").asString()) && it != target)
                    it.sendMessage(flyActivated)
            }
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        return if (args != null && args.size == 1)
            instance.server.onlinePlayers.map { it.name }.toMutableList()
        else null
    }
}