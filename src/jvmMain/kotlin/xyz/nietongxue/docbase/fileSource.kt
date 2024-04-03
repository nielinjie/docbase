package xyz.nietongxue.docbase

import java.io.File
import java.net.URL


fun listAllFiles(dir: File): List<File> {
    val result = mutableListOf<File>()
    dir.walk().forEach { file ->
        if (file.isFile) {
            result.add(file)
        }
    }
    return result
}


class FileSystemSource(val basePath: File) : ExternalSource {
    val sourceInfo = "fileSystemSource://$basePath"
    fun refDocs(): List<ReferringDoc> {
        return listAllFiles(this.basePath).map {
            refDoc(it.relativeTo(basePath), basePath, sourceInfo) }
    }
    fun updateBase(docBase: DefaultBase){
        TODO()
    }
}
