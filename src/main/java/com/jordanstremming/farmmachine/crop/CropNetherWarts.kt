package com.jordanstremming.farmhand.crop

import org.bukkit.Material
import org.bukkit.NetherWartsState
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.material.NetherWarts

class CropNetherWarts(override val block: Block) : FarmCrop {

    private val cropState = block.state.data as? NetherWarts

    override val valid: Boolean
        get() = cropState != null && block.getRelative(BlockFace.DOWN).type == Material.SOUL_SAND

    override var state: FarmCropState
        set(newState) {
            cropState?.state = when (newState) {
                FarmCropState.RIPE -> NetherWartsState.RIPE
                else -> NetherWartsState.SEEDED
            }
        }
        get() = when (cropState?.state) {
            NetherWartsState.RIPE -> FarmCropState.RIPE
            NetherWartsState.SEEDED -> FarmCropState.SEEDED
            else -> FarmCropState.GROWING
        }
}