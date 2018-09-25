package ru.hse.spb.jvm.sharkova.funproglang.interpreter

import org.antlr.v4.runtime.ParserRuleContext
import ru.hse.spb.jvm.sharkova.funproglang.FPLanguageBaseVisitor
import ru.hse.spb.jvm.sharkova.funproglang.FPLanguageParser

/**
 * Interpreter for language described by grammar in FPLanguage.g.
 * How to run: interpreter.evaluate(parser.file()),
 * where parser is an object of the generated parser class.
 * */
class Interpreter : FPLanguageBaseVisitor<Value>() {
    private val scope = Scope()

    fun evaluate(ctx: FPLanguageParser.FileContext): Int {
        return ctx.accept(this).value
    }

    override fun visitFile(ctx: FPLanguageParser.FileContext): Value {
        scope.enterNewNamespace()
        scope.addFunction(Function("println") { args ->
            println(args.joinToString(" "))
            Value()
        })

        return ctx.block().accept(this)
    }

    override fun visitBlock(ctx: FPLanguageParser.BlockContext): Value {
        for (stmt in ctx.statements) {
            val res = stmt.accept(this)
            if (res.isResult) {
                return res
            }
        }

        return Value()
    }

    override fun visitBracedBlock(ctx: FPLanguageParser.BracedBlockContext): Value {
        scope.enterNewNamespace()
        val res = ctx.block().accept(this)
        scope.leaveNamespace()
        return res
    }

    override fun visitIfStatement(ctx: FPLanguageParser.IfStatementContext): Value {
        val conditionResult = ctx.condition.accept(this)

        return when {
            conditionResult.value != 0 -> ctx.thenBody.accept(this)
            ctx.elseBody != null -> ctx.elseBody.accept(this)
            else -> Value()
        }
    }

    override fun visitWhileStatement(ctx: FPLanguageParser.WhileStatementContext): Value {
        while (true) {
            val conditionResult = ctx.condition.accept(this)
            if (conditionResult.value == 0) {
                return Value()
            }

            val res = ctx.body.accept(this)
            if (res.isResult) {
                return res
            }
        }
    }

    override fun visitReturnStatement(ctx: FPLanguageParser.ReturnStatementContext): Value {
        return Value(ctx.expression().accept(this).value, true)
    }

    override fun visitAssignment(ctx: FPLanguageParser.AssignmentContext): Value {
        val name = ctx.name.text
        if (scope.getVariable(name) == null) {
            throw InterpreterException("Variable $name not defined ${lineNumber(ctx)}")
        }

        val value = ctx.expression().accept(this)
        scope.setVariable(Variable(name, value.value))

        return Value()
    }

    override fun visitVariableDeclaration(ctx: FPLanguageParser.VariableDeclarationContext): Value {
        val name = ctx.name.text
        if (scope.getCurrentNamespace().variables.containsKey(name)) {
            throw InterpreterException("Overloading variable $name is not permitted ${lineNumber(ctx)}")
        }

        if (ctx.initialValue != null) {
            val value = ctx.initialValue.accept(this)
            scope.addVariable(Variable(name, value.value))
            return value
        }

        scope.addVariable(Variable(name, 0))
        return Value()
    }

    override fun visitFunctionDefinition(ctx: FPLanguageParser.FunctionDefinitionContext): Value {
        val name = ctx.name.text
        if (scope.getCurrentNamespace().functions.containsKey(name)) {
            throw InterpreterException("Overloading function $name is not permitted ${lineNumber(ctx)}")
        }

        scope.addFunction(Function(name) { args ->
            val params = ctx.parameters.names

            if (params.size != args.size) {
                throw InterpreterException("Mismatched arguments for function $name ${lineNumber(ctx)}")
            }

            scope.enterNewNamespace()
            params.forEachIndexed { index, token ->
                scope.addVariable(Variable(token.text, args[index]))
            }

            val result = ctx.bracedBlock().accept(this@Interpreter)
            scope.leaveNamespace()

            result
        })

        return Value()
    }

    override fun visitFunctionCallExpr(ctx: FPLanguageParser.FunctionCallExprContext): Value {
        val args = ctx.arguments().args.map { it -> it.accept(this).value }
        val name = ctx.name.text
        val res = scope.getFunction(name) ?:
                          throw InterpreterException("Function $name not found ${lineNumber(ctx)}")
        try {
            return Value(res.function.invoke(args).value)
        } catch (e: InterpreterException) {
            throw InterpreterException("Function $name threw an exception ${lineNumber(ctx)}\n${e.message}")
        }
    }

    override fun visitIdentifierExpr(ctx: FPLanguageParser.IdentifierExprContext): Value {
        val name = ctx.IDENTIFIER().text
        val value = scope.getVariable(name) ?:
                            throw InterpreterException("Undefined variable $name ${lineNumber(ctx)}")
        return Value(value.value)
    }

    override fun visitLiteralExpr(ctx: FPLanguageParser.LiteralExprContext): Value {
        return Value(ctx.LITERAL().text.toInt())
    }

    override fun visitParenthesisedExpr(ctx: FPLanguageParser.ParenthesisedExprContext): Value {
        return ctx.expression().accept(this)
    }

    override fun visitLogicalExpr(ctx: FPLanguageParser.LogicalExprContext): Value {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)
        val op = ctx.op.text

        return when (op) {
            "||" -> Value(left.value.or(right.value))
            "&&" -> Value(left.value.and(right.value))
            else -> throwUndefinedBinaryOperatorInterpreterException(op, ctx)
        }
    }

    override fun visitEqualityExpr(ctx: FPLanguageParser.EqualityExprContext): Value {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)
        val op = ctx.op.text

        return when (op) {
            "==" -> Value((left.value == right.value).toInt())
            "!=" -> Value((left.value != right.value).toInt())
            else -> throwUndefinedBinaryOperatorInterpreterException(op, ctx)
        }
    }

    override fun visitComparisonExpr(ctx: FPLanguageParser.ComparisonExprContext): Value {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)
        val op = ctx.op.text

        return when (op) {
            "<" -> Value((left.value < right.value).toInt())
            ">" -> Value((left.value > right.value).toInt())
            ">=" -> Value((left.value >= right.value).toInt())
            "<=" -> Value((left.value <= right.value).toInt())
            else -> throwUndefinedBinaryOperatorInterpreterException(op, ctx)
        }
    }

    override fun visitAdditionExpr(ctx: FPLanguageParser.AdditionExprContext): Value {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)
        val op = ctx.op.text

        return when (op) {
            "+" -> Value(left.value + right.value)
            "-" -> Value(left.value - right.value)
            else -> throwUndefinedBinaryOperatorInterpreterException(op, ctx)
        }
    }

    override fun visitMultiplicationExpr(ctx: FPLanguageParser.MultiplicationExprContext): Value {
        val left = ctx.left.accept(this)
        val right = ctx.right.accept(this)
        val op = ctx.op.text

        return when (op) {
            "*" -> Value(left.value * right.value)
            "/" -> Value(left.value / right.value)
            "%" -> Value(left.value % right.value)
            else -> throwUndefinedBinaryOperatorInterpreterException(op, ctx)
        }
    }

    override fun visitUnaryExpr(ctx: FPLanguageParser.UnaryExprContext): Value {
        val value = ctx.expression().accept(this)
        val op = ctx.op.text

        return when (op) {
            "+" -> Value(value.value)
            "-" -> Value(-value.value)
            else -> throw InterpreterException("Parsing error, undefined operator ${lineNumber(ctx)}")
        }
    }

    override fun visitParameterNames(ctx: FPLanguageParser.ParameterNamesContext): Value {
        throw RuntimeException("The process was terminated with an exception: " +
                "'parameterNames' accepting visitor")
    }

    override fun visitArguments(ctx: FPLanguageParser.ArgumentsContext): Value {
        throw RuntimeException("The process was terminated with an exception: " +
                "'arguments' accepting visitor")
    }

    private fun lineNumber(ctx: ParserRuleContext): String {
        return " (line ${ctx.start.line})"
    }

    private fun throwUndefinedBinaryOperatorInterpreterException(op: String,
                                                                 ctx: FPLanguageParser.ExpressionContext) : Value {
        throw InterpreterException("Undefined binary operator $op ${lineNumber(ctx)}")
    }

    private fun Boolean.toInt() = if (this) 1 else 0
}
