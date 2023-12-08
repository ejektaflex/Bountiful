package io.ejekta.bountiful.bridge

import java.util.*

class Bountybridge {
    companion object : BountifulSharedApi by loadService()
}

inline fun <reified T : Any> loadService(): T {
    val sl = ServiceLoader.load(T::class.java)
    val opts = sl.toList()
    return opts.first()
}
