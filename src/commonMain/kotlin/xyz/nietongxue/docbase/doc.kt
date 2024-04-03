package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import xyz.nietongxue.common.base.*
import xyz.nietongxue.docbase.SerializerM.j

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
) : Doc {



}


@Serializable
data class DependsDoc(
    override val name: String, override val content: String,
    override val attrs: Attrs<JsonElement> = mutableMapOf(),
    override val declare: DependDeclare? = null,
    override var lock: DependsLock? = null
) : Doc, HasDepends {


    override fun hashProps(): List<Pair<String, JsonElement>> {
        return super.hashProps() + ("declares" to j().encodeToJsonElement(declare))
    }


    fun checkDependSatisfied(base: DependsBase): DependSatisfied {
        if (this.declare == null) return DependSatisfied.NotDeclare
        if (this.lock == null) return DependSatisfied.SelfIsNew
        else
            if (lock!!.hash != this.getHash()) return DependSatisfied.SelfChanged // self changed, not depend changed.
        val newDepends = this.declare.let {
            base.select(it.selector)
        }
        val hashOf = newDepends.map { Depend(it.id(), it.getHash()) }
        val oldDepends = this.lock?.dependencies ?: emptyList()
        return if (hashOf == oldDepends) DependSatisfied.Satisfied else {
            diff(oldDepends, hashOf, { it.id }, { a, b -> a.hash != b.hash }).let {
                DependSatisfied.UnsatisfiedDiffs(it)
            }

        }
    }
}





