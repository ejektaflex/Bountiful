package ejektaflex.bountiful.worldgen

import com.google.common.collect.ImmutableList
import com.mojang.datafixers.util.Pair
import net.minecraft.util.ResourceLocation
import net.minecraft.world.gen.feature.jigsaw.JigsawManager
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece
import net.minecraftforge.fml.common.ObfuscationReflectionHelper
import org.apache.commons.lang3.reflect.FieldUtils
import java.util.*
import java.util.function.Supplier
import java.util.function.UnaryOperator
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author pau101, Paul Fulham. All credit for this class goes to him!
 * Original file: https://gist.github.com/pau101/7d2beb32fd77fe9a870d48ac9b81d862
 * Modified slightly for Kotlin adaptation.
 */
class JigsawJank private constructor(private val registry: OperatorRegistry) {
    fun append(name: ResourceLocation, additionalCategoryElements: () -> List<Pair<out JigsawPiece?, Int?>?>): JigsawJank {
        registry.functions[name] = UnaryOperator { pool: JigsawPattern -> appendPool(pool, additionalCategoryElements()) }
        return this
    }

    private fun appendPool(pool: JigsawPattern, additionalElements: List<Pair<out JigsawPiece?, Int?>?>): JigsawPattern {
        val fallback = pool.func_214948_a()
        val elements: ImmutableList<Pair<JigsawPiece, Int>> = Objects.requireNonNull(ObfuscationReflectionHelper.getPrivateValue(JigsawPattern::class.java, pool, "field_214952_d"), "elements")
        val placement: PlacementBehaviour = Objects.requireNonNull(ObfuscationReflectionHelper.getPrivateValue(JigsawPattern::class.java, pool, "field_214955_g"), "placement")

        return JigsawPattern(
                pool.func_214947_b(),
                fallback,
                (elements + additionalElements) as MutableList<Pair<JigsawPiece, Int>>, placement)

    }

    private class OperatorRegistry internal constructor(private val delegate: JigsawPatternRegistry) : JigsawPatternRegistry() {
        internal val functions: MutableMap<ResourceLocation, UnaryOperator<JigsawPattern>>
        override fun register(pattern: JigsawPattern) {
            if (pattern !== JigsawPattern.EMPTY) {
                delegate.register(functions.getOrDefault(pattern.func_214947_b(), UnaryOperator.identity()).apply(pattern))
            }
        }

        override fun get(name: ResourceLocation): JigsawPattern {
            return delegate[name]
        }

        init {
            functions = HashMap()
        }
    }

    companion object {
        fun create(): JigsawJank {
            val registryField = ObfuscationReflectionHelper.findField(JigsawManager::class.java, "field_214891_a")

            FieldUtils.removeFinalModifier(registryField)

            val registry: JigsawPatternRegistry = try {
                registryField[null] as JigsawPatternRegistry
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }

            val operatorRegistry = OperatorRegistry(registry)
            try {
                registryField[null] = operatorRegistry
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            }
            return JigsawJank(operatorRegistry)
        }
    }

}