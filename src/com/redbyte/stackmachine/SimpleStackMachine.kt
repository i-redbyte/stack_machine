package com.redbyte.stackmachine

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import kotlin.jvm.JvmStatic
import java.util.*
import kotlin.system.exitProcess

class SimpleStackMachine {
    private val source = ArrayList<String>()
    private val fileName: String
        get() {
            val `in` = Scanner(System.`in`)
            print("Source file name: ")
            val fn = `in`.nextLine().trim { it <= ' ' }
            if (fn.isEmpty()) {
                println("Empty source file name")
                exitProcess(0)
            }
            return fn
        }

    init {
        loadSource(fileName)
        if (source.isEmpty()) {
            println("Nothing to compile: empty source file")
            exitProcess(0)
        }
        Assembler(source)
    }

    private fun loadSource(fn: String) {
        try {
            BufferedReader(FileReader(fn)).use { br ->
                var str = br.readLine()
                while (str != null) {
                    source.add(str.uppercase(Locale.getDefault()).trim { it <= ' ' })
                    str = br.readLine()
                }
            }
        } catch (io: IOException) {
            println("Nothing to compile: source file not found")
            exitProcess(0)
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("+++++++++++++++++++++++++++++++++")
            println("+ Assembler for ToyStackMachine +")
            println("+++++++++++++++++++++++++++++++++")
            SimpleStackMachine()
        }
    }

}