package ru.hse.spb.altjvm.sharkova.texbuilder

fun formatArguments(args: List<String>): String {
    return if (args.isNotEmpty()) args.joinToString("}{", "{", "}") else ""
}

fun formatAdditionalArguments(args: Array<out String>): String {
    return if (args.isNotEmpty()) args.joinToString(",", "[", "]") else ""
}