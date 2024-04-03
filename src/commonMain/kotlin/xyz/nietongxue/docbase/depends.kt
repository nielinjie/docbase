package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import xyz.nietongxue.common.base.Diffs
import xyz.nietongxue.common.base.Hash
import xyz.nietongxue.common.base.Id

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