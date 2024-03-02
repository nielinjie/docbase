package xyz.nietongxue.docbase

import xyz.nietongxue.common.base.Id

interface Base


class MemoryBase : Base {
    val docs = mutableListOf<BasicDoc>()
    fun select(selector: DocSelector): List<Doc> {
        return docs.filter { selector.match(it) }
    }

    fun post(doc: BasicDoc) {
        if (this.docs.any { it.id() == doc.id() }) error("doc is existed, use set to modify")
        docs.add(doc)
    }

    fun set(id: Id, fn: (BasicDoc) -> BasicDoc) {
        val index = this.docs.indexOfFirst { it.id() == id }
        if (index == -1) error("not found")
        val old = this.docs[index]
        val new = fn(old)
        this.docs[index] = new
    }

    fun update(id: Id) {
        val doc = this.docs.find { it.id() == id } ?: error("no id find")
        if (doc.declare == null) return
        val newDepends = doc.declare.let {
            this.select(it.selector)
        }
        doc.lock = DependsLock(doc.getHash(), newDepends.map { Depend(it.id(), it.getHash()) })
    }

    fun checkDependOutDated(): List<Doc> {
        return this.docs.filter {
            !it.checkDependSatisfied(this)
        }
    }

}


