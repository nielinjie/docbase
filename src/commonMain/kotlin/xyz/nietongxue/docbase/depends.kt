package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import xyz.nietongxue.common.base.Hash
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.coordinate.ValueBasedPredicate

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

fun List<Matcher>.declareDepend(): DependDeclare {
    return DependDeclare(DocSelectorAnd(this))
}

fun Matcher.declareDepend(): DependDeclare {
    return DependDeclare(DocSelectorAnd(listOf(this)))
}
