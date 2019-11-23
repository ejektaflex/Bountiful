package ejektaflex.bountiful

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import org.apache.commons.lang3.tuple.Pair as ApachePair

object BountifulConfig {

    fun register() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Client.spec)
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Common.spec)
    }

    interface ISidedConfig {
        fun get()
    }

    open class SidedConfig(val builder: ForgeConfigSpec.Builder = ForgeConfigSpec.Builder()) : ISidedConfig {

        init {
            this.get()
        }

        private val specPair: ApachePair<SidedConfig, ForgeConfigSpec> by lazy {
            builder.configure { SidedConfig(it) }
        }

        val spec: ForgeConfigSpec
            get() = specPair.right

        val config: SidedConfig
            get() = specPair.left

        override fun get() {}

    }

    object Client : SidedConfig() {
        val instantDeletion = builder
                .comment("This causes the deletion slot to delete items instantly, similar to Creative Mode.")
                .define("instantDeletion", false)

        init {
            builder.comment("Client only settings").push("client")
        }

        override fun get() {
            //instantDeletion.get()
        }
    }

    object Common : SidedConfig() {
        val bountyBoardBreakable = builder
                .comment("Whether boards can break")
                .define("bountyBoardBreakable", true)

        init {
            builder.comment("Common settings for client and server").push("common")
        }

        override fun get() {
            //bountyBoardBreakable.get()
        }
    }

}