package xyz.nietongxue.docbase

import xyz.nietongxue.common.base.Id

interface Base


open class DefaultBase : Base {
    val docs = mutableListOf<Doc>()
    fun select(selector: DocSelector): List<Doc> {
        return docs.filter { selector.match(it) }
    }

    fun get(id: Id): Doc {
        return this.docs.find { it.id() == id } ?: error("not found")
    }

    fun post(doc: Doc) {
        if (this.docs.any { it.id() == doc.id() }) error("doc is existed, use set to modify")
        docs.add(doc)
    }

    fun set(id: Id, fn: (Doc) -> Doc) {
        val index = this.docs.indexOfFirst { it.id() == id }
        if (index == -1) error("not found")
        val old = this.docs[index]
        val new = fn(old)
        this.docs[index] = new
    }
}

class DependsBase : DefaultBase() {


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


