package com.jordanstremming.farmmachine

import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.inventory.DoubleChestInventory
import org.bukkit.inventory.Inventory

object Util {

    /**
     * Returns right rotation of block face
     */
    @JvmStatic
    fun getBlockRight(facing: BlockFace): BlockFace? {
        return when (facing) {
            BlockFace.NORTH ->
                BlockFace.EAST
            BlockFace.EAST ->
                BlockFace.SOUTH
            BlockFace.SOUTH ->
                BlockFace.WEST
            BlockFace.WEST ->
                BlockFace.NORTH
            else -> null
        }
    }

    /**
     * Returns DoubleChest/Chest's inventory
     */
    @JvmStatic
    fun getChestInventory(chest: Chest): Inventory {
        val inventory = chest.inventory
        return inventory as? DoubleChestInventory ?: return inventory
    }

}