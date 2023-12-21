package me.nelmin.kotlin.command.team

import me.nelmin.kotlin.SpigotUltimate
import me.nelmin.kotlin.text.TextBuilder
import net.kyori.adventure.text.TextComponent
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class VanishCommand : CommandExecutor, TabCompleter {
    private val instance = SpigotUltimate.instance
    private val cmds = instance.configManager.commands

    private val seePerm = cmds.get("vanish", "see_permission").asString()
    private val staffPerm = instance.configManager.config.get("staff_permission").asString()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage(TextBuilder(cmds.get("vanish", "no", "player").asString()).pluginPrefix().getColorized())
            return true
        }

        if (!sender.hasPermission(cmds.get("vanish", "permission").asString())) {
            sender.sendMessage(TextBuilder(cmds.get("vanish", "no", "permission").asString()).pluginPrefix().getColorized())
            return true
        }

        val target: Player? = if (args.isNullOrEmpty()) sender else instance.server.getPlayer(args[1])

        if (target == null || !target.isOnline) {
            sender.sendMessage(TextBuilder(instance.configManager.config.get("player_not_found").asString()).pluginPrefix().getColorized())
            return true
        }

        val vanishActivated = TextBuilder(
            cmds.get("vanish", "messages", "activated").asString()
        ).replace("%player_name%", target.name).pluginPrefix().getColorized()

        val vanishDeactivated = TextBuilder(
            cmds.get("vanish", "messages", "deactivated").asString()
        ).replace("%player_name%", target.name).pluginPrefix().getColorized()

        hideOrShow(target, sender, vanishActivated, vanishDeactivated)

        return true
    }

    private fun hideOrShow(target: Player, sender: Player, activated: TextComponent, deactivated: TextComponent) {
        if (instance.vanishedPlayers.contains(target)) {
            instance.server.onlinePlayers.forEach {
                if ((it.hasPermission(staffPerm) || it.hasPermission(seePerm)) && it != target && it != sender)
                    it.sendMessage(deactivated)

                it.showPlayer(instance, target)
            }
            instance.vanishedPlayers.remove(target)

            if (target == sender)
                sender.sendMessage(deactivated)
            else {
                sender.sendMessage(deactivated)
                target.sendMessage(deactivated)
            }
        }
        else {
            instance.server.onlinePlayers.forEach {
                if ((it.hasPermission(staffPerm) || it.hasPermission(seePerm)) && it != target && it != sender) it.sendMessage(activated)
                else it.hidePlayer(instance, target)
            }
            instance.vanishedPlayers.add(target)

            if (target == sender)
                sender.sendMessage(activated)
            else {
                sender.sendMessage(activated)
                target.sendMessage(activated)
            }
        }
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