package ru.hse.spb.jvm.sharkova.funproglang.interpreter

import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

data class Value(val value: Int = 0, val isResult: Boolean = false)

data class Function(val name: String, val function: (List<Int>) -> Value)

data class Variable(val name: String, val value: Int)

class InterpreterException(override var message: String): Exception(message)

class ThrowExceptionListener : BaseErrorListener() {
    override fun syntaxError(recognizer: Recognizer<*, *>?, offendingSymbol: Any?, line: Int, charPositionInLine: Int, msg: String?, e: RecognitionException?) {
        super.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
        throw InterpreterException("Incorrect symbol $offendingSymbol on line $line at position $charPositionInLine.\n $msg")
    }
}