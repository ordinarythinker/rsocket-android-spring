package com.rsockettester

import java.io.PrintWriter
import java.io.StringWriter

fun Exception.asString(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    this.printStackTrace(pw)
    return sw.toString()
}