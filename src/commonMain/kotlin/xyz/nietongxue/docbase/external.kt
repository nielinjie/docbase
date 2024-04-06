package xyz.nietongxue.docbase

interface Importer : Source{
    fun raw(path: String):ByteArray
}


