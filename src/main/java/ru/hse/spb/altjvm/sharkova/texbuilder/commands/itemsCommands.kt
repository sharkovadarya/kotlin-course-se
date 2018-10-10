package ru.hse.spb.altjvm.sharkova.texbuilder.commands

import ru.hse.spb.altjvm.sharkova.texbuilder.TexWriter

open class ItemsCommand(name: String,
                        args: List<String>,
                        vararg additionalArgs: String) : CommandWithChildren(name, args, *additionalArgs) {
    fun item(init: Item.() -> Unit) = initElement(Item(), init)

}

class Item : CommandWithChildren("", emptyList()) {
    override fun render(writer: TexWriter) {
        writer.write("\\item ")
        renderChildren(writer)
        writer.writeEmptyLine()
    }
}

class Itemize(vararg additionalArgs: String)
    : ItemsCommand("itemize", emptyList(), *additionalArgs)

class Enumerate(vararg additionalArgs: String)
    : ItemsCommand("enumerate", emptyList(), *additionalArgs)
