package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.*

interface Doc : WithHash, HasDepends {
    fun id(): Id
    val attrs: Attrs<JsonElement>
    val name: String
    val content: String
}

@Serializable
class BasicDoc(
    override val name: String,
    override val content: String,
    override val attrs: Attrs<JsonElement> = mutableMapOf()
) : Doc {
    @Transient
    override val depend = DependsManagement()
    override fun id(): Id {
        val path = this.attrs["path"]?.jsonPrimitive?.content
        return if (path != null) {
            Path.fromString(path).append(this.name)
        } else {
            Path.fromString(this.name)
        }.asString()
    }

    override fun getHash(): Hash {
        return hashObject(this, serializer())
    }
}



