package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import xyz.nietongxue.common.base.Attrs
import xyz.nietongxue.common.base.Hash
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.SerializerM.j
import java.io.File

@Serializable
data class Referring(
    val refPath: String,
    val sourceInfo: String,
    val fileContentHash: Hash
)

@Serializable
data class ReferringDoc(
    override val name: String,
    override val content: String,
    override val attrs: Attrs<JsonElement> = mutableMapOf(),
    val referring: Referring
) : Doc {


    override fun hashProps(): List<Pair<String, JsonElement>> {
        return super.hashProps() + ("referring" to j().encodeToJsonElement(referring))
    }
}

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