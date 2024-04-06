package xyz.nietongxue.docbase

interface Importer : Source,BaseListener {
    fun raw(path: String):ByteArray
}




