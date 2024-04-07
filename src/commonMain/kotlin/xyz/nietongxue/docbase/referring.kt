package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import xyz.nietongxue.common.base.Attrs
import xyz.nietongxue.common.base.Hash
import xyz.nietongxue.common.base.j

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

