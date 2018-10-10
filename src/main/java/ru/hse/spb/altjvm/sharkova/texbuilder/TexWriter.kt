package ru.hse.spb.altjvm.sharkova.texbuilder

import java.io.OutputStream
import java.lang.StringBuilder

interface TexWriter {
    fun write(text: String)

    fun writeEmptyLine() {
        write(System.lineSeparator())
    }
}

class StringBuilderWriter(private val builder: StringBuilder) : TexWriter {
    override fun write(text: String) {
        builder.append(text)
    }
}

class OutputStreamWriter(private val stream: OutputStream) : TexWriter {
    override fun write(text: String) {
        stream.write(text.toByteArray())
    }
}

