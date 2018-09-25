package ru.hse.spb.jvm.sharkova.funproglang

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.hse.spb.jvm.sharkova.funproglang.interpreter.Interpreter
import ru.hse.spb.jvm.sharkova.funproglang.interpreter.ThrowExceptionListener
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.system.exitProcess
import java.lang.Exception

fun main(args: Array<String>) {
    if (args.isEmpty() || !Files.exists(Paths.get(args[0]))) {
        println("No file found, program terminated.")
        exitProcess(0)
    }

    val lexer = FPLanguageLexer(CharStreams.fromFileName(args[0]))
    val parser = FPLanguageParser(BufferedTokenStream(lexer))
    lexer.addErrorListener(ThrowExceptionListener())
    parser.addErrorListener(ThrowExceptionListener())

    val interpreter = Interpreter()
    try {
        interpreter.evaluate(parser.file())
    } catch (e: Exception) {
        println("Failed to interpret file. Cause:\n${e.message}")
    }
}
