package xyz.nietongxue.docbase

import kotlinx.serialization.json.JsonPrimitive
import xyz.nietongxue.common.base.hashBytes
import java.io.File

fun refDoc(relativePath: File, base: File, sourceInfo: String): ReferringDoc {

    return ReferringDoc(
        relativePath.name,
        "",
        mapOf("path" to JsonPrimitive("$sourceInfo/$relativePath")),
        Referring(
            relativePath.path, sourceInfo, hashBytes(File(base, relativePath.path).readBytes())
        )
    )
}