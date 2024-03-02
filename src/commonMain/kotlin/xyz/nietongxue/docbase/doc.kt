package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.*


interface Doc : WithHash, HasDepends {
    fun id(): Id
    val attrs: Attrs<JsonElement>
    val name: String
    val content: String
}


@Serializable
data class BasicDoc(
    override val name: String, override val content: String,
    override val attrs: Attrs<JsonElement> = mutableMapOf(),
    override val declare: DependDeclare? = null,
    override var lock: DependsLock? = null
) : Doc {
    override fun id(): Id {
        val path = this.attrs["path"]?.jsonPrimitive?.content
        return if (path != null) {
            Path.fromString(path).append(this.name)
        } else {
            Path.fromString(this.name)
        }.asString()
    }

    override fun getHash(): Hash {
        return hashProperties(
            "name" to name,
            "content" to content,
            "attrs" to attrs,
            "declares" to Json.encodeToJsonElement(declare),
        )
    }


    fun checkDependSatisfied(base: MemoryBase): Boolean {
        if (this.declare == null) return true
        return if (lock == null) {
            false
        } else {
            if (lock!!.hash != this.getHash()) return false
            val newDepends = this.declare.let {
                base.select(it.selector)
            }
            val hashOf = newDepends.map { Depend(it.id(), it.getHash()) }
            hashOf == this.lock!!.dependencies
        }
    }
}



