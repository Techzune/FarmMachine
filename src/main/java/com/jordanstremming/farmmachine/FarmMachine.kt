package com.jordanstremming.farmmachine

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class FarmMachine : JavaPlugin() {

	// the plugin's config
	val pluginConfig: Config = Config()

	override fun onEnable() {
		// register events
		server.pluginManager.registerEvents(BlockListener(), this)

		// save config file
		saveDefaultConfig()

		// yay!
		logger.info("FarmMachine is ready to work!")
	}

	override fun onDisable() {
		logger.info("FarmMachine says goodbye...")
	}

	fun sendMessage(player: Player, message: String) {
		player.sendMessage(ChatColor.DARK_GREEN.toString() + "[FarmMachine] " + ChatColor.WHITE.toString() + message)
	}

	// keep an instance of the plugin
	init {
		instance = this
	}

	companion object {
		lateinit var instance: FarmMachine
	}

}
