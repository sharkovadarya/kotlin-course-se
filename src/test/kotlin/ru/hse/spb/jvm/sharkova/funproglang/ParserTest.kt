package ru.hse.spb.jvm.sharkova.funproglang

import org.antlr.v4.runtime.*
import org.junit.Test
import ru.hse.spb.jvm.sharkova.funproglang.interpreter.InterpreterException
import ru.hse.spb.jvm.sharkova.funproglang.interpreter.ThrowExceptionListener


internal class ParserTest {
    private val listener = ThrowExceptionListener()

    private fun testProgram(programText: String) {
        val lexer = FPLanguageLexer(CharStreams.fromString(programText))
        val parser = FPLanguageParser(BufferedTokenStream(lexer))
        lexer.addErrorListener(listener)
        parser.addErrorListener(listener)

        parser.file()
    }

    @Test
    fun testCorrectProgram() {
        testProgram("var a = 30\nprintln(1 + a)")
    }

    @Test(expected = InterpreterException::class)
    fun testIncorrectSymbol() {
        testProgram("prin%%%t(1)")
    }

    @Test(expected = InterpreterException::class)
    fun testIdentifierStartingWithDigit() {
        testProgram("var 1a = 30")
    }

    @Test(expected = InterpreterException::class)
    fun testUndefinedBinaryOperation() {
        testProgram("var a = 2 ^ 2")
    }

    @Test
    fun testEmptyFunction() {
        testProgram("fun function() {}")
    }

    @Test(expected = InterpreterException::class)
    fun testIncorrectParameterListExtraComma() {
        testProgram("fun function(param1,, param2) {}")
    }

    @Test(expected = InterpreterException::class)
    fun testIncorrectParameterListAbsentComma() {
        testProgram("fun function(param1 param2) {}")
    }

    @Test
    fun testArguments() {
        testProgram("fun addition(a, b) {return a + b}" +
                "addition(2, addition(2 + 3, 2))")
    }

    @Test
    fun testCorrectLiteral() {
        testProgram("30\nvar a = -40")
    }
}