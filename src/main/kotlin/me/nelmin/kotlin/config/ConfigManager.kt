package me.nelmin.kotlin.config

import com.osiris.dyml.Yaml
import lombok.Getter
import me.nelmin.kotlin.SpigotUltimate
import java.io.File
import java.io.FileOutputStream

class ConfigManager {
    private val instance = SpigotUltimate.instance

    @Getter
    val config: Yaml =
        Yaml(instance.javaClass.getClassLoader().getResourceAsStream("config.yml"), FileOutputStream(File(instance.folder, "config.yml")))

    @Getter
    val commands: Yaml =
        Yaml(instance.javaClass.getClassLoader().getResourceAsStream("commands.yml"), FileOutputStream(File(instance.folder, "commands.yml")))

    init {
        this.config.load()
        this.commands.load()

        this.config.save()
        this.commands.save()
    }
}