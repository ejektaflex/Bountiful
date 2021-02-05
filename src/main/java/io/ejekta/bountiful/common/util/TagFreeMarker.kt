package io.ejekta.bountiful.common.util

import net.minecraft.nbt.PositionTracker
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagReader
import net.minecraft.text.LiteralText
import java.io.DataInput
import java.io.DataOutput

object TagFreeMarker : Tag {

    val READER: TagReader<TagFreeMarker> = object : TagReader<TagFreeMarker> {
        override fun read(dataInput: DataInput, i: Int, positionTracker: PositionTracker): TagFreeMarker {
            positionTracker.add(64L)
            return TagFreeMarker
        }

        override fun getCrashReportName(): String {
            return "FREEMARKER"
        }

        override fun getCommandFeedbackName(): String {
            return "TAG_FREEMARKER"
        }

        override fun isImmutable(): Boolean {
            return true
        }
    }

    override fun write(output: DataOutput?) {
        // do nothing
    }

    override fun getType() = (73).toByte() // our magic number

    override fun getReader() = READER

    override fun copy() = this

    override fun toText(indent: String?, depth: Int) = LiteralText.EMPTY
}