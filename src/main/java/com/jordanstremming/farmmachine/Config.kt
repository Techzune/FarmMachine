package com.jordanstremming.farmmachine

import org.bukkit.configuration.file.FileConfiguration

class Config {

	private val config: FileConfiguration
		get() = FarmMachine.instance.config

	/**
	 * if farmers should till soil automatically
	 */
	val tillSoil: Boolean
		get() = config.getBoolean("tillSoil")

	/**
	 * the farm grid size, x-coordinate
	 */
	val farmSize: Int
		get() = config.getInt("farmSize")

	/**
	 * if farmers should plant seeds when farmland allows
	 */
	val plantWhenEmpty: Boolean
		get() = config.getBoolean("plantWhenEmpty")

	/**
	 * the seed mapping from config "cropMapping"
	 */
	fun getCropFromSeed(crop: String): String? {
		return config.getString("cropMapping.$crop")
	}

	/**
	 * the message string of message type
	 */
	fun getMessage(message: String): String {
		return config.getString("messages.$message") ?: ""
	}
}
