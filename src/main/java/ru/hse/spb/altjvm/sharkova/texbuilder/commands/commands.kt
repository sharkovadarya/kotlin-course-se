package ru.hse.spb.altjvm.sharkova.texbuilder.commands

import ru.hse.spb.altjvm.sharkova.texbuilder.*

abstract class BasicCommand(private val name: String,
                            private val args: List<String>,
                            private vararg val additionalArgs: String) : Element {
    override fun render(writer: TexWriter) {
        writer.write("\\$name")
        writer.write(formatAdditionalArguments(additionalArgs))
        writer.write(formatArguments(args))
        writer.writeEmptyLine()
    }
}

abstract class CommandWithChildren(private val name: String,
                                   private val args: List<String>,
                                   private vararg val additionalArgs: String) : Element {
    private val children: MutableList<Element> = mutableListOf()

    // basic commands use 'begin'-'end'; some don't, thus this method is overridden in some children
    override fun render(writer: TexWriter) {
        writer.write("\\begin{$name}")
        writer.write(formatAdditionalArguments(additionalArgs))
        writer.write(formatArguments(args))
        writer.writeEmptyLine()

        renderChildren(writer)

        writer.write("\\end{$name}")
        writer.writeEmptyLine()
    }

    operator fun String.unaryPlus() {
        children.add(TextElement(this))
    }

    protected fun <T : Element> initElement(element: T, init: T.() -> Unit): T {
        element.init()
        children.add(element)
        return element
    }

    protected fun renderChildren(writer: TexWriter) {
        children.forEach { it.render(writer) }
    }
}

open class TexCommand(name: String,
                      args: List<String>,
                      vararg additionalArgs: String)
    : CommandWithChildren(name, args, *additionalArgs) {

    fun frame(frameTitle: String, vararg additionalArgs: String,
              init: Frame.() -> Unit) = initElement(Frame(frameTitle, *additionalArgs), init)

    fun itemize(vararg additionalArgs: String, init: Itemize.() -> Unit) =
            initElement(Itemize(*additionalArgs), init)

    fun enumerate(vararg additionalArgs: String, init: Enumerate.() -> Unit) =
            initElement(Enumerate(*additionalArgs), init)

    fun customTag(name: String, vararg additionalArgs: String, init: CustomTag.() -> Unit) =
            initElement(CustomTag(name, *additionalArgs), init)

    fun mathMode(init: MathMode.() -> Unit) = initElement(MathMode(), init)

    fun flushleft(init: FlushLeft.() -> Unit) = initElement(FlushLeft(), init)

    fun flushright(init: FlushRight.() -> Unit) = initElement(FlushRight(), init)

    fun center(init: Center.() -> Unit) = initElement(Center(), init)
}


class Frame(frameTitle: String, vararg additionalArgs: String)
    : TexCommand("frame", listOf(frameTitle), *additionalArgs)

class CustomTag(name: String, vararg additionalArgs: String)
    : TexCommand(name, emptyList(), *additionalArgs)

class MathMode : CommandWithChildren("", emptyList()) {
    override fun render(writer: TexWriter) {
        writer.write("$$ ")
        renderChildren(writer)
        writer.write("$$")
        writer.writeEmptyLine()
    }
}

