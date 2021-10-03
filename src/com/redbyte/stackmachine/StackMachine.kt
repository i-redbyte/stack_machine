package com.redbyte.stackmachine

import java.lang.IndexOutOfBoundsException
import java.util.*
import kotlin.system.exitProcess

class StackMachine(private val obj: ArrayList<Int>) {
    private val memory = IntArray(MEMORY_SIZE)
    private val dstack = IntArray(256)
    private val rstack = IntArray(256)
    private var loaded = 0
    private var dsp = -1
    private var rsp = -1
    private var pc = 0
    private var cycles = 0
    private val symb = SymbolTable()

    init {
        if (obj.size > MEMORY_SIZE) {
            println("Too long object code")
            exitProcess(0)
        }
        loaded = loadObject()
        run()
        printStatus()
    }

    private fun loadObject(): Int {
        var counter = 0
        while (counter < obj.size) {
            memory[counter] = obj[counter]
            counter++
        }
        return counter
    }

    private fun run() {
        var command = 0
        var buffer = 0
        println("Run...")
        try {
            while (true) {
                command = memory[pc++]
                ++cycles
                if (command >= 0) {
                    dstack[++dsp] = command
                    continue
                }
                when (command) {
                    -1 -> {
                        buffer = dstack[dsp]
                        dstack[++dsp] = buffer
                    }
                    -2 -> dsp--
                    -3 -> {
                        buffer = dstack[dsp - 1]
                        dstack[dsp - 1] = dstack[dsp]
                        dstack[dsp] = buffer
                    }
                    -4 -> {
                        buffer = dstack[dsp - 1]
                        dstack[++dsp] = buffer
                    }
                    -5 -> {
                        buffer = dstack[dsp]
                        dsp--
                        rstack[++rsp] = buffer
                    }
                    -6 -> {
                        buffer = rstack[rsp]
                        rsp--
                        dstack[++dsp] = buffer
                    }
                    -7 -> {
                        dstack[dsp - 1] = dstack[dsp - 1] + dstack[dsp]
                        dsp--
                    }
                    -8 -> {
                        dstack[dsp - 1] = dstack[dsp - 1] - dstack[dsp]
                        dsp--
                    }
                    -9 -> {
                        dstack[dsp - 1] = dstack[dsp - 1] * dstack[dsp]
                        dsp--
                    }
                    -10 -> {
                        val q = dstack[dsp - 1] / dstack[dsp]
                        val r = dstack[dsp - 1] % dstack[dsp]
                        dstack[dsp - 1] = r
                        dstack[dsp] = q
                    }
                    -11 -> dstack[dsp] = -dstack[dsp]
                    -12 -> dstack[dsp] = Math.abs(dstack[dsp])
                    -13 -> {
                        dstack[dsp - 1] = dstack[dsp - 1] and dstack[dsp]
                        dsp--
                    }
                    -14 -> {
                        dstack[dsp - 1] = dstack[dsp - 1] or dstack[dsp]
                        dsp--
                    }
                    -15 -> {
                        dstack[dsp - 1] = dstack[dsp - 1] xor dstack[dsp]
                        dsp--
                    }
                    -16 -> dstack[dsp] = dstack[dsp] shl 1
                    -17 -> dstack[dsp] = dstack[dsp] shr 1
                    -18 -> pc = dstack[dsp--]
                    -19 -> if (dstack[dsp - 1] < 0) {
                        pc = dstack[dsp--]
                        dsp--
                    } else dsp -= 2
                    -20 -> if (dstack[dsp - 1] == 0) {
                        pc = dstack[dsp--]
                        dsp--
                    } else dsp -= 2
                    -21 -> if (dstack[dsp - 1] > 0) {
                        pc = dstack[dsp--]
                        dsp--
                    } else dsp -= 2
                    -22 -> {
                        rstack[++rsp] = pc
                        pc = dstack[dsp]
                        dsp--
                    }
                    -23 -> pc = rstack[rsp--]
                    -24 -> {
                        buffer = dstack[dsp]
                        dstack[dsp] = memory[buffer]
                    }
                    -25 -> {
                        buffer = dstack[dsp--]
                        memory[buffer] = dstack[dsp]
                        dsp--
                    }
                    -26 -> dstack[++dsp] = readInt()
                    -27 -> println("${dstack[dsp--]}")
                    -28 -> println("${dstack[dsp--].toChar()}")
                    -29 -> dstack[++dsp] = pc - 1
                    -30 -> if (dsp < 0) {
                        dstack[++dsp] = 0
                    } else {
                        dstack[++dsp] = dsp
                    }
                    -31 -> Unit
                    -40 -> println("Program completed")
                    else -> {
                        println("Undefined operation code $command")
                        println("Program aborted")
                    }
                }
            }
        } catch (ex: IndexOutOfBoundsException) {
            println("Memory empty or destroyed")
        }
    }

    private fun readInt(): Int {
        val `in` = Scanner(System.`in`)
        var value = 0
        print("Number: ")
        value = `in`.nextInt()
        return value
    }

    private fun printStatus() {
        if (dsp >= 0) {
            println()
            println("DATA STACK:")
            println("+---------+-----------+")
            println("|   DSP   |   Value   |")
            println("+---------+-----------+")
            for (i in 0..dsp) {
                println("|$i|${dstack[i]}|")
            }
            println("+---------+-----------+")
        }
        if (rsp >= 0) {
            println()
            println("RETURN STACK:")
            println("+---------+-----------+")
            println("|   RSP   |   Value   |")
            println("+---------+-----------+")
            for (i in 0..rsp) {
                println("|$i|${rstack[i]}|")
            }
            println("+---------+-----------+")
        }
        val entrys = SymbolTable.symtable.entries
        println()
        println("CONTENTS OF")
        entrys.forEach { (key, value) -> if (value >= 0) println("[$key] = $ memory[it]") }
        println("   dsp = $dsp")
        println("   rsp = $rsp")
        println("    pc = $pc")
        println("cycles = $cycles")
    }

    companion object {
        const val MEMORY_SIZE = 65536
    }

}