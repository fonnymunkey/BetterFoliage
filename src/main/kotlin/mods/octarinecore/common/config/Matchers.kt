package mods.octarinecore.common.config

import mods.octarinecore.client.resource.getLines
import mods.octarinecore.client.resource.resourceManager
import mods.octarinecore.metaprog.getJavaClass
import net.minecraft.block.Block
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.Logger

interface IBlockMatcher {
    fun matchesClass(block: Block): Boolean
    fun matchingClass(block: Block): Class<*>?
}

class SimpleBlockMatcher(vararg val classes: Class<*>) : IBlockMatcher {
    override fun matchesClass(block: Block) = matchingClass(block) != null

    override fun matchingClass(block: Block): Class<*>? {
        val blockClass = block.javaClass
        classes.forEach { if (it.isAssignableFrom(blockClass)) return it }
        return null
    }
}

class ConfigurableBlockMatcher(val logger: Logger, val location: ResourceLocation) : IBlockMatcher {

    val blackList = mutableListOf<Class<*>>()
    val whiteList = mutableListOf<Class<*>>()
//    override fun convertValue(line: String) = getJavaClass(line)

    override fun matchesClass(block: Block): Boolean {
        val blockClass = block.javaClass
        blackList.forEach { if (it.isAssignableFrom(blockClass)) return false }
        whiteList.forEach { if (it.isAssignableFrom(blockClass)) return true }
        return false
    }

    override fun matchingClass(block: Block): Class<*>? {
        val blockClass = block.javaClass
        blackList.forEach { if (it.isAssignableFrom(blockClass)) return null }
        whiteList.forEach { if (it.isAssignableFrom(blockClass)) return it }
        return null
    }

    fun readDefaults() {
        blackList.clear()
        whiteList.clear()
        resourceManager.getAllResources(location).forEach { resource ->
            logger.debug("Reading resource $location from pack ${resource.packName}")
            resource.getLines().map{ it.trim() }.filter { !it.startsWith("//") && it.isNotEmpty() }.forEach { line ->
                if (line.startsWith("-")) getJavaClass(line.substring(1))?.let { blackList.add(it) }
                else getJavaClass(line)?.let { whiteList.add(it) }

            }
        }
    }
}

data class ModelTextureList(val modelLocation: ResourceLocation, val textureNames: List<String>) {
    constructor(vararg args: String) : this(ResourceLocation(args[0]), listOf(*args).drop(1))
}

class ModelTextureListConfiguration(val logger: Logger, val location: ResourceLocation) {
    val modelList = mutableListOf<ModelTextureList>()
    fun readDefaults() {
        resourceManager.getAllResources(location).forEach { resource ->
            logger.debug("Reading resource $location from pack ${resource.packName}")
            resource.getLines().map{ it.trim() }.filter { !it.startsWith("//") && it.isNotEmpty() }.forEach { line ->
                val elements = line.split(",")
                modelList.add(ModelTextureList(ResourceLocation(elements.first()), elements.drop(1)))
            }
        }
    }
}