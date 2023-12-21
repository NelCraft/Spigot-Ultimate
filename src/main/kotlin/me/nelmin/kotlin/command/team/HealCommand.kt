package me.nelmin.kotlin.command.team

import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import org.bukkit.attribute.Attribute
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class HealCommand : CommandExecutor, TabCompleter {
    private val instance = SpigotUltimate.instance
    private val cmds = instance.configManager.commands

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextBuilder(cmds.get("heal", "no", "player").asString()).pluginPrefix().getColorized())
            return true
        }

        if (!sender.hasPermission(cmds.get("heal", "permission").asString())) {
            sender.sendMessage(TextBuilder(cmds.get("heal", "no", "permission").asString()).pluginPrefix().getColorized())
            return true
        }

        val target: Player? = if (args.isNullOrEmpty()) sender else instance.server.getPlayer(args[1])

        if (target == null || !target.isOnline) {
            sender.sendMessage(TextBuilder(instance.configManager.config.get("player_not_found").asString()).pluginPrefix().getColorized())
            return true
        }

        target.health = target.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        target.foodLevel = 20

        if (target.uniqueId == sender.uniqueId)
            sender.sendMessage(TextBuilder(cmds.get("heal", "messages", "self").asString()).pluginPrefix().getColorized())
        else
            sender.sendMessage(TextBuilder(cmds.get("heal", "no", "other").asString())
                .replaceFirst("%player_name%", target.name)
                .replaceFirst("%player_name%", sender.name)
                .pluginPrefix().getColorized())

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