package me.nelmin.kotlin.text

import me.clip.placeholderapi.PlaceholderAPI
import me.nelmin.kotlin.SpigotUltimate
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

class TextBuilder(text: String) {
    private var text: String = ""

    init {
        this.text = text
    }

    fun getText(): TextComponent = Component.text(text)
    fun getColorized(): TextComponent = LegacyComponentSerializer.legacy('&').deserialize(text)

    fun withPlaceholders(player: Player): TextBuilder {
        this.text = PlaceholderAPI.setPlaceholders(player, this.text)
        return this
    }

    fun appendBefore(text: String): TextBuilder {
        this.text = text + this.text
        return this
    }
    fun appendAfter(text: String): TextBuilder {
        this.text += text
        return this
    }

    fun replace(old: String, new: String): TextBuilder {
        this.text = this.text.replace(old, new)
        return this
    }
    fun replaceFirst(old: String, new: String): TextBuilder {
        this.text = this.text.replaceFirst(old, new)
        return this
    }

    fun spyFormat(target: Player, commandName: String): TextBuilder {
        this.text = this.text
            .replace("%spy_prefix%", SpigotUltimate.instance.configManager.config.get("prefixes", "spy").asString())
            .replace("%player_name%", target.name)
            .replace("%command_name%", commandName.replaceFirst("/", ""))

        return this
    }

    fun pluginPrefix(): TextBuilder {
        this.text = "${SpigotUltimate.instance.configManager.config.get("prefixes", "plugin").asString()} ${this.text}"
        return this
    }
    fun spyPrefix(): TextBuilder {
        this.text = "${SpigotUltimate.instance.configManager.config.get("prefixes", "spy").asString()} ${this.text}"
        return this
    }
}