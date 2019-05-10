package com.jordanstremming.farmmachine

import com.jordanstremming.farmhand.crop.FarmCrop
import com.jordanstremming.farmhand.crop.FarmCropState
import com.jordanstremming.farmmachine.Util.getBlockRight
import com.jordanstremming.farmmachine.Util.getChestInventory
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.block.data.Ageable
import org.bukkit.block.data.type.Observer
import java.util.concurrent.ThreadLocalRandom

class Farmer(private val observerBlock: Block, private val observer: Observer, chestBlock: Block) {

    private val chest: Chest = chestBlock.state as Chest

    fun update() {
        harvest()
    }

    private fun harvest() {
        // get start of grid
        val size = FarmMachine.instance.pluginConfig.farmSize
        val right = getBlockRight(observer.facing) ?: return
        val left = right.oppositeFace
        val startCrops = observerBlock.getRelative(observer.facing).getRelative(right, (size - 1) / 2)

        // iterate through square
        var curCropsX: Block
        for (x in 0 until size) {
            curCropsX = startCrops.getRelative(left, x)
            for (y in 0 until size)
                harvestCrops(curCropsX.getRelative(observer.facing, y))
        }
    }

    private fun plantCrops(cropsBlock: Block) {
        // if allowed to plant
        if (!FarmMachine.instance.pluginConfig.plantWhenEmpty)
            return

        // get soil
        val soilBlock = cropsBlock.getRelative(BlockFace.DOWN)

        // check if can plant
        if (cropsBlock.type != Material.AIR)
            return

        // check if if should till
        if (soilBlock.type != Material.FARMLAND)
            tillSoil(soilBlock)


        // iterate through chest inventory and find new crops
        val invIter = getChestInventory(chest).iterator()
        var crops: FarmCrop?
        while (invIter.hasNext()) {
            // grab the next item and attempt to get crop type
            val item = invIter.next() ?: continue
            val cropType = FarmMachine.instance.pluginConfig.getCropFromSeed(item.type.toString()) ?: continue

            // sugar cane and cactus are not supported
            if (cropType == "SUGAR_CANE" || cropType == "CACTUS")
                continue

            // set the crop
            cropsBlock.type = Material.valueOf(cropType)
            crops = FarmCrop.fromBlock(cropsBlock)
            if (!crops.valid) {
                // if it failed to place as a crop; cancel
                cropsBlock.type = Material.AIR
                continue
            }

            // set the crop to seeded and remove the item from chest
            crops.state = FarmCropState.SEEDED
            item.amount = item.amount - 1
            return

        }
    }

    private fun harvestCrops(cropsBlock: Block) {
        // get crops
        val crops = FarmCrop.fromBlock(cropsBlock)

        // plant crops if non-existent
        if (!crops.valid) {
            plantCrops(cropsBlock)
            return
        }

        // don't harvest if not ripe
        if (crops.state != FarmCropState.RIPE) {
            return
        }

        // get chest inventory
        val chestInventory = getChestInventory(chest)

        // add crops drops
        val drops = cropsBlock.drops
        for (drop in drops)
            if (chestInventory.addItem(drop).size > 0) return

        // break without drops
        try {
            cropsBlock.world.playSound(cropsBlock.location, Sound.BLOCK_WET_GRASS_BREAK, 10f, .5f)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // set to seed age
        val age = (cropsBlock.blockData as Ageable)
        age.age = 0
        cropsBlock.blockData = age

        // add seeds based on random counter
        val seedDrops = cropsBlock.drops.toMutableList()
        if (seedDrops.iterator().hasNext()) {
            // get seeds
            val seed = cropsBlock.drops.iterator().next()

            // randomnly generate seeds
            var numSeeds = 0
            for (i in 0..2)
                if (ThreadLocalRandom.current().nextInt(15) <= 7)
                    numSeeds++
            seed.amount = seed.amount * numSeeds - 1

            if (seed.amount > 0)
                if (chestInventory.addItem(seed).size > 0) return
        }
    }

    private fun tillSoil(soilBlock: Block): Boolean {
        // check if soil
        if (soilBlock.type != Material.DIRT)
            return false

        // check if tillSoil is true
        if (!FarmMachine.instance.pluginConfig.tillSoil)
            return false

        // convert soil to farmland
        soilBlock.type = Material.FARMLAND
        try {
            soilBlock.world.playSound(soilBlock.location, Sound.ITEM_HOE_TILL, 10f, .5f)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

}
