package mods.betterfoliage.client.render

import mods.betterfoliage.BetterFoliageMod
import mods.betterfoliage.client.Client
import mods.betterfoliage.client.config.Config
import mods.octarinecore.client.render.*
import mods.octarinecore.common.Int3
import mods.octarinecore.common.Rotation
import mods.octarinecore.random
import net.minecraft.block.BlockPackedIce
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.BufferBuilder
import net.minecraft.util.BlockRenderLayer
import net.minecraft.util.EnumFacing.*
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.apache.logging.log4j.Level.INFO

@SideOnly(Side.CLIENT)
class RenderPackedIce : AbstractBlockRenderingHandler(BetterFoliageMod.MOD_ID) {

    val noise = simplexNoise()
    val packedIceIcon = iconSet(BetterFoliageMod.LEGACY_DOMAIN, "blocks/better_packedice_%d")
    val packedIceModel = modelSet(64) { modelIdx ->
        verticalRectangle(x1 = -0.5, z1 = 0.5, x2 = 0.5, z2 = -0.5, yTop = -0.5,
        yBottom = -0.5 - random(Config.packedIce.heightMin, Config.packedIce.heightMax))
        .setAoShader(faceOrientedAuto(overrideFace = DOWN, corner = cornerAo(Axis.Y)))
        .setFlatShader(faceOrientedAuto(overrideFace = DOWN, corner = cornerFlat))
        .toCross(UP) { it.move(xzDisk(modelIdx) * Config.packedIce.hOffset) }.addAll()
    }

    override fun afterPreStitch() {
        Client.log(INFO, "Registered ${packedIceIcon.num} packedIce textures")
    }

    override fun isEligible(ctx: BlockContext): Boolean {
        if (!Config.enabled || !Config.packedIce.enabled) return false
        return Config.blocks.packedIce.matchesClass(ctx.block) &&
                noise[ctx.pos] < Config.packedIce.population
    }

    override fun render(ctx: BlockContext, dispatcher: BlockRendererDispatcher, renderer: BufferBuilder, layer: BlockRenderLayer): Boolean {
        val baseRender = renderWorldBlockBase(ctx, dispatcher, renderer, layer)
        if (!layer.isCutout) return baseRender

        if (ctx.blockState(down1).isOpaqueCube) return baseRender

        modelRenderer.updateShading(Int3.zero, allFaces)

        val rand = ctx.semiRandomArray(2)
        modelRenderer.render(
            renderer,
            packedIceModel[rand[0]],
            Rotation.identity,
            icon = { _, qi, _ -> packedIceIcon[rand[qi and 1]]!! },
            postProcess = noPost
        )

        return true
    }
}