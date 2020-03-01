package ejektaflex.bountiful.data

import net.minecraftforge.resource.IResourceType

enum class BountifulResourceType : IResourceType {

    // Currently, only ALL is being used. There's not really a reason to only reload one type, because it's so fast.
    ALL,
    DECREES,
    POOLS

}