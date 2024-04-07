package xyz.nietongxue.docbase.depends

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import xyz.nietongxue.common.base.Attrs
import xyz.nietongxue.common.base.diff
import xyz.nietongxue.common.base.j
import xyz.nietongxue.docbase.Doc


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