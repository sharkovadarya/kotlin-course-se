package ru.hse.spb.altjvm.sharkova.texbuilder.commands

open class AlignmentCommand(name: String) : TexCommand(name, emptyList())

class FlushLeft : AlignmentCommand("flushleft")

class FlushRight : AlignmentCommand("flushright")

class Center : AlignmentCommand("center")