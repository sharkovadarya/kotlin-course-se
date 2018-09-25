package ru.hse.spb.jvm.sharkova.funproglang.interpreter

import java.util.*

class Scope {
    private val namespaces = Stack<Namespace>()

    fun getCurrentNamespace(): Namespace {
        return namespaces.peek()
    }

    fun enterNewNamespace() {
        namespaces.push(Namespace())
    }

    fun leaveNamespace() {
        namespaces.pop()
    }

    fun addFunction(function: Function) {
        getCurrentNamespace().addFunction(function)
    }

    fun addVariable(variable: Variable) {
        getCurrentNamespace().setVariable(variable)
    }

    fun setVariable(variable: Variable) {
        namespaces.findLast { it -> it.variables.containsKey(variable.name) }?.setVariable(variable)
    }

    fun getFunction(name: String): Function? {
        return (namespaces.findLast {it -> it.functions.containsKey(name)})?.functions?.get(name)
    }

    fun getVariable(name: String): Variable? {
        return (namespaces.findLast {it -> it.variables.containsKey(name)})?.variables?.get(name)
    }
}

class Namespace {
    val functions = HashMap<String, Function>()
    val variables = HashMap<String, Variable>()

    fun addFunction(function: Function) {
        functions[function.name] = function
    }

    fun setVariable(variable: Variable) {
        variables[variable.name] = variable
    }

}