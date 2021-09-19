package io.ejekta.kambrik.gui.toolkit

data class KRect(val x: Int, val y: Int, val w: Int, val h: Int) {
    fun isInside(ix: Int, iy: Int): Boolean {
        return Companion.isInside(ix, iy, x, y, w, h)
    }

    companion object {
        fun isInside(ix: Int, iy: Int, x: Int, y: Int, w: Int, h: Int): Boolean {
            return ix >= x && ix <= x + w
                    && iy >= y && iy <= y + h
        }
    }
}