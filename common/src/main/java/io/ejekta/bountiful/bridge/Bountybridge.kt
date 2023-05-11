package io.ejekta.bountiful.bridge

import java.util.ServiceLoader

class Bountybridge {
    companion object : BountifulSharedApi by loadService(BountifulSharedApi::class.java)
}

inline fun <reified T : Any> loadService(jc: Class<T>): T {
    val sl = ServiceLoader.load(T::class.java)
    println("SL: $sl")
    val opts = sl.toList()
    println("OPTS: $opts")
    return opts.first()
}
