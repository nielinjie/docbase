package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import xyz.nietongxue.common.base.Hash
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.coordinate.Selector
@Serializable
data class DependsLock(val hash: Hash, val dependencies: List<Depend>)
@Serializable
data class Depend(val id: Id, val hash: Hash)
@Serializable
data class DependDeclare(val selector: DocSelector)

interface HasDepends {
    val depend :DependsManagement
}

/*
depend 是doc的性质还是base的性质？
 */
class DependsManagement {
    val locks  = mutableListOf<DependsLock>()
    val dependDeclares = mutableListOf<DependDeclare>()

}
