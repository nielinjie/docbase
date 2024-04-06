package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import xyz.nietongxue.common.base.*
import xyz.nietongxue.common.base.Serializing.j

interface Doc : WithHash {
    fun id(): Id{
        val path = this.attrs["path"]?.jsonPrimitive?.content
        return if (path != null) {
            Path.fromString(path).append(this.name)
        } else {
            Path.fromString(this.name)
        }.asString()
    }
    val attrs: Attrs<JsonElement>
    val name: String
    val content: String

    fun hashProps(): List<Pair<String, JsonElement>> = listOf(
        "name" to JsonPrimitive(name),
        "content" to JsonPrimitive(content),
        "attrs" to j().encodeToJsonElement<Attrs<JsonElement>>(attrs)
    )

    override fun getHash(): Hash {
        return hashProperties(*hashProps().toTypedArray())
    }
}

@Serializable
data class SimpleDoc(
    override val name: String,
    override val content: String,
    override val attrs: Attrs<JsonElement> = mutableMapOf()
) : Doc







