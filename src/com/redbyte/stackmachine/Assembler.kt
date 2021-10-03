package com.redbyte.stackmachine

import java.lang.Exception
import java.util.ArrayList
import kotlin.system.exitProcess

class Assembler(private val source: ArrayList<String>) {
    private val st = SymbolTable()
    private val ct = OpcodeTable()
    private val obj = ArrayList<Int>()
    private var currLine: String = ""
    private var token: String = ""
    private var passNumber = 0
    private var loc = 0
    private var lineNumber = 0
    private var tokenType = 0
    private var errorCount = 0

    init {
        parse()
        printObjectCode()
        st.printSymtable()
        if (obj.isEmpty()) {
            println("Nothing to run")
            exitProcess(0)
        } else {
            StackMachine(obj)
        }
    }

    private fun parse() {
        passNumber = 1
        println("Parsing (pass $passNumber): ")
        passOne()
        checkErrors()
        passNumber = 2
        println("Parsing (pass $passNumber): ")
        passTwo()
        checkErrors()
    }

    private fun checkErrors() {
        if (errorCount != 0) {
            println("detected $errorCount error(s)")
            exitProcess(0)
        } else {
            println("ok")
        }
    }

    private fun passOne() {
        lineNumber = 0
        loc = 0
        for (s in source) {
            lineNumber++
            currLine = s
            tokenType = getToken()
            first()
        }
    }

    private fun first() {
        when (tokenType) {
            TT_EOL -> {
            }
            TT_LABEL -> {
                st.putSymbol(token, loc)
                tokenType = getToken()
                first()
            }
            TT_CODE, TT_DATA -> tokenType = getToken()
            TT_ERROR -> errorCount++
        }
    }

    private fun passTwo() {
        lineNumber = 0
        loc = 0
        for (s in source) {
            lineNumber++
            currLine = s
            tokenType = getToken()
            second()
        }
    }

    private fun second() {
        when (tokenType) {
            TT_LABEL -> {
                tokenType = getToken()
                second()
            }
            TT_EOL, TT_CODE, TT_DATA -> {
            }
            TT_ERROR -> errorCount++
        }
    }

    private fun getToken(): Int {
        if (currLine.contains(";")) {
            currLine = currLine.substring(0, currLine.indexOf(";")).trim { it <= ' ' }
        }
        if (currLine.contains("#")) {
            currLine = currLine.substring(0, currLine.indexOf("#")).trim { it <= ' ' }
        }
        if (currLine.isEmpty()) {
            return TT_EOL
        }
        if (currLine.startsWith(":")) {
            token = if (currLine.contains(" ")) {
                currLine.substring(1, currLine.indexOf(" "))
            } else {
                currLine.substring(1)
            }
            return if (isValidLabel(token)) {
                currLine = if (currLine.length > token.length) {
                    currLine.substring(token.length + 1).trim { it <= ' ' }
                } else {
                    ""
                }
                TT_LABEL.also { tokenType = it }
            } else {
                TT_ERROR.also { tokenType = it }
            }
        }
        if (currLine.isEmpty()) {
            return TT_EOL.also { tokenType = it }
        }
        token = currLine
        when (passNumber) {
            1 -> {
                loc++
                return TT_EOL.also { tokenType = it }
            }
            2 -> {
                if (isNumber(token)) {
                    obj.add(parseWord(token))
                    loc++
                    return TT_DATA.also { tokenType = it }
                }
                if (st.lookupSymbol(token)) {
                    obj.add(st.getValue(token))
                    loc++
                    return TT_DATA.also { tokenType = it }
                }
                if (ct.isMnemonic(token)) {
                    obj.add(ct.getOpcode(token))
                    loc++
                    return TT_CODE.also { tokenType = it }
                }
                error("undefined symbol or code")
                return TT_ERROR.also { tokenType = it }
            }
        }
        return TT_EOL
    }

    private fun isValidLabel(lbl: String): Boolean {
        when (passNumber) {
            1 -> {
                if (lbl.isEmpty()) {
                    error("label expected")
                    return false
                }
                if (st.lookupSymbol(lbl)) {
                    error("duplicated label")
                    return false
                }
            }
            2 -> {
            }
        }
        return true
    }

    private fun isNumber(operand: String): Boolean {
        try {
            parseWord(operand)
        } catch (ex: Exception) {
            return false
        }
        return true
    }

    private fun parseWord(lit: String): Int {
        return lit.toInt(10)
    }

    private fun error(msg: String) {
        println("ERROR: $msg [line: $lineNumber, pass: $passNumber]")
    }

    private fun printObjectCode() {
        var initAddr = 0
        if (obj.isEmpty()) return
        println()
        println("OBJECT CODE")
        println("-----------------------")
        val i: Iterator<*> = obj.iterator()
        while (i.hasNext()) {
            println("${initAddr++}        ${i.next()}")
        }
        println()
    }

    companion object {
        const val TT_EOL = 0
        const val TT_CODE = 1
        const val TT_LABEL = 2
        const val TT_DATA = 3
        const val TT_ERROR = -1
    }
}