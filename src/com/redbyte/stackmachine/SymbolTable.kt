package com.redbyte.stackmachine

import java.util.TreeMap

typealias Entries = MutableMap<String, Int>

class SymbolTable {

    init {
        symtable = TreeMap()
    }

    fun lookupSymbol(name: String): Boolean = symtable[name] != null

    fun putSymbol(name: String, value: Int) {
        if (lookupSymbol(name)) return
        symtable[name] = value
    }

    fun getValue(name: String): Int {
        if (lookupSymbol(name).not()) return UNDEFSYMBOL
        return symtable[name] ?: UNDEFSYMBOL
    }

    fun printSymtable() {
        val entries: Set<Map.Entry<String, Int>> = symtable.entries
        if (entries.isEmpty()) return
        println()
        println("SYMBOL TABLE")
        println("-----------------------")
        for ((key) in entries) {
            println("$key ${getValue(key)}")
        }
        println()
    }

    companion object {
        const val UNDEFSYMBOL = -1
        var symtable: Entries = mutableMapOf()
    }
}