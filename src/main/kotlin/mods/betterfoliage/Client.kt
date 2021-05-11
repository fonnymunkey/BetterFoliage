package mods.betterfoliage

import mods.betterfoliage.chunk.ChunkOverlayManager
import mods.betterfoliage.config.BlockConfig
import mods.betterfoliage.integration.OptifineCustomColors
import mods.betterfoliage.integration.ShadersModIntegration
import mods.betterfoliage.render.block.vanilla.StandardDirtDiscovery
import mods.betterfoliage.render.block.vanilla.StandardDirtKey
import mods.betterfoliage.render.block.vanilla.StandardDirtModel
import mods.betterfoliage.render.block.vanilla.StandardGrassDiscovery
import mods.betterfoliage.render.block.vanilla.StandardGrassModel
import mods.betterfoliage.render.block.vanilla.StandardLeafDiscovery
import mods.betterfoliage.render.block.vanilla.StandardLeafModel
import mods.betterfoliage.render.block.vanilla.StandardLilypadDiscovery
import mods.betterfoliage.render.block.vanilla.StandardLilypadModel
import mods.betterfoliage.render.block.vanilla.StandardMyceliumDiscovery
import mods.betterfoliage.render.block.vanilla.StandardMyceliumModel
import mods.betterfoliage.render.block.vanilla.StandardSandDiscovery
import mods.betterfoliage.render.block.vanilla.StandardSandModel
import mods.betterfoliage.render.lighting.AoSideHelper
import mods.betterfoliage.render.particle.LeafWindTracker
import mods.betterfoliage.resource.discovery.BakeWrapperManager
import mods.betterfoliage.resource.discovery.BlockTypeCache
import mods.betterfoliage.resource.generated.GeneratedTexturePack
import mods.betterfoliage.texture.LeafParticleRegistry
import net.minecraft.block.BlockState
import net.minecraft.block.Blocks
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraftforge.common.ForgeConfig

/**
 * Object responsible for initializing (and holding a reference to) all the infrastructure of the mod
 * except for the call hooks.
 */
object Client {
    val generatedPack = GeneratedTexturePack("bf_gen", "Better Foliage generated assets")
    var blockTypes = BlockTypeCache()

    val suppressRenderErrors = mutableSetOf<BlockState>()

    fun init() {
        // discoverers
        BetterFoliageMod.bus.register(BakeWrapperManager)
        BetterFoliageMod.bus.register(LeafParticleRegistry)
        listOf(
            StandardLeafDiscovery,
            StandardGrassDiscovery,
            StandardDirtDiscovery,
            StandardMyceliumDiscovery,
            StandardSandDiscovery,
            StandardLilypadDiscovery
        ).forEach {
            BakeWrapperManager.discoverers.add(it)
        }

        // init singletons
        val singletons = listOf(
            AoSideHelper,
            BlockConfig,
            ChunkOverlayManager,
            LeafWindTracker
        )

        val modelSingletons = listOf(
            StandardLeafModel.Companion,
            StandardGrassModel.Companion,
            StandardDirtModel.Companion,
            StandardMyceliumModel.Companion,
            StandardSandModel.Companion,
            StandardLilypadModel.Companion
        )

        // init mod integrations
        val integrations = listOf(
            ShadersModIntegration,
            OptifineCustomColors
        )
    }
}

