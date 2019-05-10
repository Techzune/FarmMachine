package com.jordanstremming.farmhand.crop

import org.bukkit.Material
import org.bukkit.block.Block

interface FarmCrop {

    /**
     * the block associated with crop
     */
    val block: Block

    /**
     * if the crop exists as crop type
     */
    val valid: Boolean

    /**
     * the current crop growing stage
     */
    var state: FarmCropState

    /**
     * static methods
     */
    companion object {
        @JvmStatic
        fun fromBlock(block: Block): FarmCrop {
            return when(block.type) {
                Material.NETHER_WART -> CropNetherWarts(block)
                else -> CropCrops(block)
            }
        }
    }
}

