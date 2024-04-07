package xyz.nietongxue.docbase

import java.io.File


fun listAllFiles(dir: File): List<File> {
    val result = mutableListOf<File>()
    dir.walk().forEach { file ->
        if (file.isFile) {
            result.add(file)
        }
    }
    return result
}


class FileSystemImporter(val basePath: File) : Importer {

    override val sourceInfo = "fileSystemSource:$basePath"
    override fun raw(path: String): ByteArray {
        return File(basePath, path).readBytes()
    }

    fun refDocs(): List<ReferringDoc> {
        return listAllFiles(this.basePath).map {
            refDoc(it.relativeTo(basePath), basePath, sourceInfo)
        }
    }

    override fun onOpen(base: Base) {
        updateBase(base as DefaultBase)
    }

    override fun updateBase(docBase: DefaultBase) {
        val refDocs = refDocs()
        refDocs.forEach {
            //id不存在，就是新的path/name文件。更新。
            //id存在
            //path/name相同，但是hash不同，更新 - referring - content hash不同。
            //          sourceInfo不同，无法处理。exception。不更新。不会出现，id里面有path，path里面有sourceInfo
            //          sourceInfo相同，更新。
            if (docBase.exists(it.id())) {
                val old = docBase.get(it.id())
                if (old is ReferringDoc) {
                    if (old.referring.fileContentHash != it.referring.fileContentHash) {
                        docBase.postOrSet(it)
                    } else {
                        //do nothing
                    }
                } else {
                    error("id exists, but not ReferringDoc")
                }
            } else {
                docBase.post(it)
            }
        }
        //TODO 实现同步，对于已经删除的文件，需要删除对应的doc

        docBase.docs.filterIsInstance<ReferringDoc>().filter { it.referring.sourceInfo == this.sourceInfo }
            .forEach { fromBase ->
                if (refDocs.none { fromFile ->
                        fromBase.id() == fromFile.id()
                    }) {
                    docBase.delete(fromBase.id())
                }
            }
    }
}

