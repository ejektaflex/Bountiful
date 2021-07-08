package ejektaflex.bountiful

object BountifulInfo {
    const val ADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    const val MODID = "bountiful"
    const val NAME = "Bountiful"
    const val VERSION = "2.2.3"

    const val FORGE_DEP = "required-after:forge@[14.23.4.2768,15.0.0.0);"
    const val DEPENDS = "${FORGE_DEP}required-after:forgelin@[1.8.0,1.9.0);"

    const val CLIENT = "ejektaflex.bountiful.proxy.ClientProxy"
    const val SERVER = "ejektaflex.bountiful.proxy.CommonProxy"
}
