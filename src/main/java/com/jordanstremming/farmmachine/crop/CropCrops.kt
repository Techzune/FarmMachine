package com.jordanstremming.farmhand.crop

import org.bukkit.CropState
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.material.Crops

class CropCrops(override val block: Block) : FarmCrop {

    private val cropState = block.state.data as? Crops

    override val valid: Boolean
        get() = cropState != null && block.getRelative(BlockFace.DOWN).type == Material.FARMLAND

    override var state: FarmCropState
        set(newState) {
            cropState?.state = when (newState) {
                FarmCropState.RIPE -> CropState.RIPE
                else -> CropState.SEEDED
            }
        }
        get() = when (cropState?.state) {
            CropState.RIPE -> FarmCropState.RIPE
            CropState.SEEDED -> FarmCropState.SEEDED
            else -> FarmCropState.GROWING
        }
}