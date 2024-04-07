package xyz.nietongxue.docbase.depends

import kotlinx.serialization.Serializable
import xyz.nietongxue.common.base.Change
import xyz.nietongxue.common.base.Diffs
import xyz.nietongxue.common.base.Hash
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.docbase.*

@Serializable
data class DependsLock(val hash: Hash, val dependencies: List<Depend>)

@Serializable
data class Depend(val id: Id, val hash: Hash)

@Serializable
data class DependDeclare(val selector: DocSelector)

interface HasDepends {
    val declare: DependDeclare?
    val lock: DependsLock?

}

class DependsBase(
    worker: Persistence, listeners: List<Listener>,
) : DefaultBase(worker, listeners) {


    fun update(id: Id) {
        val doc = this.docs.find { it.id() == id } ?: error("no id find")
        require(doc is DependsDoc) {
            "doc is not DependsDoc"
        }
        if (doc.declare == null) return
        val newDepends = doc.declare.let {
            this.select(it.selector)
        }
        doc.lock = DependsLock(doc.getHash(), newDepends.map { Depend(it.id(), it.getHash()) })
        docListeners.forEach {
            it.onChanged(DocChangeEvent(doc, Change.Changed))
        }
        persistence.save()
    }

    fun checkDependOutDated(): List<Pair<Doc, DependSatisfied>> {
        return this.docs.mapNotNull {
            if (it !is DependsDoc) return@mapNotNull null
            val satisfied = it.checkDependSatisfied(this)
            if (satisfied is DependSatisfied.UnsatisfiedDiffs || satisfied is DependSatisfied.Unsatisfied) {
                it to satisfied
            } else {
                null
            }
        }
    }
}

fun DocSelector.declareDepend(): DependDeclare {
    return DependDeclare(this)
}

fun List<DimensionMatcher>.declareDepend(): DependDeclare {
    return DependDeclare(DocSelectorAnd(this))
}

fun DimensionMatcher.declareDepend(): DependDeclare {
    return DependDeclare(DocSelectorAnd(listOf(this)))
}


sealed class DependSatisfied {
    data object Satisfied : DependSatisfied()
    sealed class Unsatisfied(val reason: String) : DependSatisfied()
    class UnsatisfiedDiffs(val diffs: Diffs) : Unsatisfied("diffs")
    data object SelfChanged : Unsatisfied("self changed")
    data object SelfIsNew : Unsatisfied("self is new")
    data object NotDeclare : DependSatisfied()
}