package com.jordanstremming.farmmachine

import com.jordanstremming.farmmachine.Util.getBlockRight
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.block.data.Directional
import org.bukkit.block.data.type.Observer
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockRedstoneEvent
import org.bukkit.event.player.PlayerInteractEvent

class BlockListener : Listener {

	// plugin instances
	private val plugin = FarmMachine.instance
	private val config
		get() = plugin.pluginConfig

	/**
	 * Observer update check
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	fun onRedstoneEvent(event: BlockRedstoneEvent) {

		// get block
		val observerBlock = event.block

		// get observer
		val observer = observerBlock.blockData as? Observer ?: return

		// only accept rising current
		if (event.newCurrent < event.oldCurrent)
			return

		// get RIGHT-SIDE face
		val signFace: BlockFace = getBlockRight(observer.facing) ?: return

		// check for RIGHT-SIDE face is Sign
		val sign = observerBlock.getRelative(signFace).state as? Sign ?: return
		if (!sign.getLine(0).equals("[farm]", true))
			return

		// check for LEFT-SIDE face is Chest
		val chestBlock = observerBlock.getRelative(signFace.oppositeFace)
		if (chestBlock.type != Material.CHEST)
			return

		// create Farmer object and update
		val farmer = Farmer(observerBlock, observer, chestBlock)
		farmer.update()

	}

	/**
	 * Player-based check on Farmer
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	fun onPlayerInteract(event: PlayerInteractEvent) {

		// cancel if necessary
		if (event.isCancelled) return
		val player = event.player
		val block = event.clickedBlock ?: return

		// check for clicking a Sign
		val sign = block.state as? Sign ?: return
		if (!sign.getLine(0).equals("[farm]", ignoreCase = true))
			return

		// convert to Directional and get Observer
		val signDirectional = block.blockData as? Directional ?: return
		val observerBlock = block.getRelative(signDirectional.facing.oppositeFace)
		val observer = observerBlock.blockData as? Observer

		// check for Observer
		if (observer == null) {
			plugin.sendMessage(player, config.getMessage("farmerNoObserver"))
			return
		}

		if (observer.facing == BlockFace.DOWN || observer.facing == BlockFace.UP) {
			plugin.sendMessage(player, config.getMessage("farmerNoUpDown"))
			return
		}

		// check for Chest beside Observer
		val chestBlock = observerBlock.getRelative(signDirectional.facing.oppositeFace)
		if (chestBlock.type != Material.CHEST) {
			plugin.sendMessage(player, config.getMessage("farmerNoChest"))
			return
		}

		// valid Farmer
		plugin.sendMessage(player, config.getMessage("farmerValid"))

		// create Farmer and update
		val farmer = Farmer(observerBlock, observer, chestBlock)
		farmer.update()
	}

}
