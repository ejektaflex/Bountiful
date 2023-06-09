package io.ejekta.bountiful.bridge

import java.util.ServiceLoader

class Bountybridge {
    companion object : BountifulSharedApi by loadService()
}

inline fun <reified T : Any> loadService(): T {
    val sl = ServiceLoader.load(T::class.java)
    val opts = sl.toList()
    return opts.first()
}
