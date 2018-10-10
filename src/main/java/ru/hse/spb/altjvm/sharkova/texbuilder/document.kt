package ru.hse.spb.altjvm.sharkova.texbuilder

import ru.hse.spb.altjvm.sharkova.texbuilder.commands.BasicCommand
import ru.hse.spb.altjvm.sharkova.texbuilder.commands.TexCommand
import java.io.OutputStream

@DslMarker
annotation class TexDslMarker

@TexDslMarker
interface Element {
    fun render(writer: TexWriter)
}

class TextElement(private val text: String) : Element {
    override fun render(writer: TexWriter) {
        writer.write(text)
        writer.writeEmptyLine()
    }
}

class DocumentClass(documentClass: String, vararg additionalArgs: String)
    : BasicCommand("documentclass", listOf(documentClass), *additionalArgs)

class UsePackage(packageName: String, vararg additionalArgs: String)
    : BasicCommand("usepackage", listOf(packageName), *additionalArgs)

class Document : TexCommand("document", emptyList()) {
    private lateinit var docClass: DocumentClass

    private val packages: MutableList<UsePackage> = mutableListOf()

    fun documentClass(documentClassName: String, vararg additionalArgs: String) {
        if (this::docClass.isInitialized) {
            throw TexBuilderException("Multiple document classes are not allowed.")
        }

        docClass = DocumentClass(documentClassName, *additionalArgs)
    }

    fun usepackage(packageName: String, vararg additionalArgs: String) {
        packages.add(UsePackage(packageName, *additionalArgs))
    }

    override fun render(writer: TexWriter) {
        if (!this::docClass.isInitialized) {
            throw TexBuilderException("No document class found")
        }

        docClass.render(writer)
        packages.forEach { it.render(writer) }
        super.render(writer)
    }

    fun toOutputStream(stream: OutputStream) {
        render(OutputStreamWriter(stream))
    }

    override fun toString() = buildString { render(StringBuilderWriter(this)) }
}

fun document(init: Document.() -> Unit) = Document().apply(init)