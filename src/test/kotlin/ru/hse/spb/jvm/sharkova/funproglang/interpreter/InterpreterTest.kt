package ru.hse.spb.jvm.sharkova.funproglang.interpreter

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.hse.spb.jvm.sharkova.funproglang.FPLanguageLexer
import ru.hse.spb.jvm.sharkova.funproglang.FPLanguageParser
import java.io.PrintStream
import java.io.ByteArrayOutputStream


internal class InterpreterTest {

    private var baos = ByteArrayOutputStream()
    private lateinit var printStream: PrintStream
    private lateinit var systemOut: PrintStream

    @Before
    fun redirectSystemOut() {
        printStream = PrintStream(baos)
        systemOut = System.out
        System.setOut(printStream)
    }

    @After
    fun restoreSystemOut() {
        System.out.flush()
        System.setOut(systemOut)
    }

    // evaluation testing

    private val listener = ThrowExceptionListener()

    private fun runTestProgram(lexer: FPLanguageLexer) {
        val parser = FPLanguageParser(BufferedTokenStream(lexer))
        lexer.addErrorListener(listener)
        parser.addErrorListener(listener)

        val interpreter = Interpreter()

        interpreter.evaluate(parser.file())
    }

    private fun testProgramFromString(programText: String) {
        val lexer = FPLanguageLexer(CharStreams.fromString(programText))
        return runTestProgram(lexer)
    }

    private fun testProgramFromFile(filename: String) {
        val lexer = FPLanguageLexer(CharStreams.fromFileName(filename))
        return runTestProgram(lexer)
    }

    @Test
    fun testPrintln() {
        testProgramFromString("println(1)")
        assertEquals("1", baos.toString().trim())
    }

    @Test
    fun testAddition() {
        testProgramFromString("println(30 + 59 + 11 - 32 + 32 - 321 - 98 + 342)")
        assertEquals("23", baos.toString().trim())
    }

    @Test
    fun testParenthesisedExpression() {
        testProgramFromString("println((((30)) + 18) * 2 + (2 - 8 * (21)))")
        assertEquals("-70", baos.toString().trim())
    }

    @Test
    fun testUnaryExpression() {
        testProgramFromString("println((-30) + 1)")
        assertEquals("-29", baos.toString().trim())
    }

    @Test
    fun testFunctionWithConditionalStatements() {
        testProgramFromString("fun function(a, b, c) {" +
                "var d = 22" +
                "var e = b * 3" +
                "while (d >= c) {e = e - 1 \n d = d - 2}\n" +
                "if (e == c) {println(e)} else {println(d)}\n" +
                "}" +
                "function(20, 6, 12)\n")
        assertEquals("12", baos.toString().trim())
    }

    @Test
    fun testRecursiveFunction() {
        testProgramFromString("fun rec(a) { if (a <= 2) { return 2 } return rec(a - 2) + 2 * rec(a - 1) }" +
                "println(rec(5))")
        assertEquals("34", baos.toString().trim())
    }

    @Test
    fun testBinaryExpressions() {
        testProgramFromString("var condition = (30 * 2 == 15 * 4) && (12 - 10 != 9 % 5 / 2 + 1)" +
                "if (condition == 1) {println(1)} else {println(0)}")
        assertEquals("1", baos.toString().trim())
    }

    @Test
    fun testNestedFunctionsAndVariables() {
        testProgramFromFile("src/test/resources/nested.txt")
        assertEquals("11${System.lineSeparator()}8${System.lineSeparator()}89${System.lineSeparator()}",
                baos.toString())
    }

    @Test(expected = InterpreterException::class)
    fun testOverloadingFunction() {
        testProgramFromString("fun function() {}\nfun function(a) {println(a)}")
    }

    @Test
    fun testVariableReassignment() {
        testProgramFromString("var a = 30\n a = 40\n println(a)")
        assertEquals("40", baos.toString().trim())
    }

    @Test(expected = InterpreterException::class)
    fun testVariableRedeclaration() {
        testProgramFromString("var a = 30\n a = 40\n var a = 41\n println(a)")
    }

    @Test(expected = InterpreterException::class)
    fun testUndefinedFunction() {
        testProgramFromString("function(30)")
    }

    @Test(expected = InterpreterException::class)
    fun testUndeclaredVariable() {
        testProgramFromString("fun function(a, b) {return a - 3 * b}\n var a = 21" +
                "\nfunction(a, b)")
    }

    @Test
    fun testLineComments() {
        testProgramFromString("//println(20)\n println(40)")
        assertEquals("40", baos.toString().trim())
    }

    @Test
    fun testFunctionUnavailableOutsideNamespace() {
        // try-catch block to check line number
        try {
            testProgramFromFile("src/test/resources/function_out_of_namespace.txt")
        } catch (e: InterpreterException) {
            assertTrue(e.message.contains("(line 11)"))
        }
    }

    @Test
    fun testPrintlnMultipleArguments() {
        testProgramFromString("println(2, 12, 13, 4, 15, 27)\n println(2, 11)")
        assertEquals("2 12 13 4 15 27${System.lineSeparator()}2 11${System.lineSeparator()}",
                baos.toString())
    }

    @Test
    fun testDefaultReturnValue() {
        testProgramFromString("fun function(a) {println(a)}\n println(function(2))")
        assertEquals("2${System.lineSeparator()}0${System.lineSeparator()}", baos.toString())
    }

    @Test
    fun testLargerProgram() {
        testProgramFromFile("src/test/resources/larger_program.txt")
        assertEquals("13", baos.toString().trim())
    }

}