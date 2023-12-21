package me.nelmin.kotlin.command.team

import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SpyCommand : CommandExecutor, TabCompleter {
    private val instance = SpigotUltimate.instance
    private val cmds = instance.configManager.commands

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextBuilder(cmds.get("spy", "no", "player").asString()).pluginPrefix().getColorized())
            return true
        }

        if (!sender.hasPermission(cmds.get("spy", "permission").asString())) {
            sender.sendMessage(TextBuilder(cmds.get("spy", "no", "permission").asString()).pluginPrefix().getColorized())
            return true
        }

        if (args.isNullOrEmpty()) {
            sender.sendMessage(TextBuilder(cmds.get("spy", "syntax").asString()).pluginPrefix().getColorized())
            return true
        }

        val target = instance.server.getPlayer(args[0])

        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(TextBuilder(instance.configManager.config.get("player_not_found").asString()).pluginPrefix().getColorized())
            return true
        }

        val targetSpys = instance.spyPlayers[target.uniqueId]

        if (!targetSpys.isNullOrEmpty() && targetSpys.contains(sender.uniqueId)) {
            targetSpys.remove(sender.uniqueId).also {
                if (targetSpys.isEmpty()) instance.spyPlayers.remove(target.uniqueId)
            }
            sender.sendMessage(TextBuilder(cmds.get("spy", "messages", "deactivated").asString()).replace("%player_name%", target.name).pluginPrefix().getColorized())
            return true
        }

        if (target.uniqueId == sender.uniqueId) {
            sender.sendMessage(TextBuilder(cmds.get("spy", "messages", "cant_spy", "self").asString()).pluginPrefix().getColorized())
            return true
        }

        if (target.hasPermission(cmds.get("spy", "bypass_permission").asString())) {
            sender.sendMessage(TextBuilder(cmds.get("spy", "messages", "cant_spy", "bypass").asString()).replace("%player_name%", target.name).pluginPrefix().getColorized())
            return true
        }

        if (targetSpys.isNullOrEmpty())
            instance.spyPlayers[target.uniqueId] = mutableSetOf(target.uniqueId)
        else
            instance.spyPlayers[target.uniqueId]!!.add(sender.uniqueId)

        sender.sendMessage(TextBuilder(cmds.get("spy", "messages", "activated").asString()).replace("%player_name%", target.name).pluginPrefix().getColorized())
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