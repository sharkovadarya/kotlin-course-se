package ru.hse.spb.altjvm.sharkova.texbuilder

import java.util.*

fun main(args: Array<String>) {
    val rows = Arrays.asList(1, 2, 4)

    document {
        documentClass("beamer")
        usepackage("babel", "russian" /* varargs */)
        frame("frametitle", "arg1", "arg2") {
            itemize {
                for (row in rows) {
                    item { +"$row text" }
                }
            }

            // begin{pyglist}[language=kotlin]...\end{pyglist}
            customTag("pyglist", "language", "kotlin") {
                +"""
               |val a = 1
               |
            """
            }
        }
    }.toOutputStream(System.out)
}