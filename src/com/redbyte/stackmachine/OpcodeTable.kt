package com.redbyte.stackmachine

import java.util.HashMap
import com.redbyte.stackmachine.OpcodeTable

class OpcodeTable {
    private val optable: MutableMap<String, Int?> = HashMap()

    init {
        optable["DUP"] = -1
        optable["DROP"] = -2
        optable["SWAP"] = -3
        optable["OVER"] = -4
        optable["DTR"] = -5
        optable["RTD"] = -6
        optable["ADD"] = -7
        optable["SUB"] = -8
        optable["MUL"] = -9
        optable["DIV"] = -10
        optable["NEG"] = -11
        optable["ABS"] = -12
        optable["AND"] = -13
        optable["OR"] = -14
        optable["XOR"] = -15
        optable["SHL"] = -16
        optable["SHR"] = -17
        optable["BR"] = -18
        optable["BRN"] = -19
        optable["BRZ"] = -20
        optable["BRP"] = -21
        optable["CALL"] = -22
        optable["RET"] = -23
        optable["LOAD"] = -24
        optable["SAVE"] = -25
        optable["IN"] = -26
        optable["OUTN"] = -27
        optable["OUTC"] = -28
        optable["LPC"] = -29
        optable["DEPTH"] = -30
        optable["NOP"] = -31
        optable["HALT"] = -40
    }

    fun isMnemonic(mnemo: String): Boolean {
        return optable[mnemo] != null
    }

    fun getOpcode(mnemo: String): Int {
        return if (optable[mnemo] != null) {
            optable[mnemo]!!
        } else UNDEFCODE
    }

    companion object {
        const val UNDEFCODE = -255
    }
}