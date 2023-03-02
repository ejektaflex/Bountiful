package io.ejekta.kudzu

@DslMarker
annotation class KudzuMarker

/*
@KudzuMarker
class KudzuBuilder(val func: KudzuVine.() -> Unit) {
    init {
        KudzuVine().apply(func)
    }
}

 */

@KudzuMarker
fun kudzu(func: KudzuVine.() -> Unit): KudzuVine {
    return KudzuVine().apply(func)
}

