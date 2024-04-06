package xyz.nietongxue.docbase

import xyz.nietongxue.common.base.Id

interface Base


interface DocListener {
    fun onChanged(doc: Doc, change: Change)
}
interface BaseListener {
    fun onOpen(base: Base)
}

open class DefaultBase(
    val persistence: Persistence,
    val listeners: MutableList<DocListener>,
    val baseListeners: MutableList<BaseListener>
) : Base {
    val docs = mutableListOf<Doc>()

    init {
        persistence.setBase(this)
        persistence.load()
        baseListeners.forEach { it.onOpen(this) }
    }

    fun select(selector: DocSelector): List<Doc> {
        return docs.filter { selector.match(it) }
    }

    fun get(id: Id): Doc {
        return this.docs.find { it.id() == id } ?: error("not found")
    }

    fun exists(id: Id): Boolean {
        return this.docs.any { it.id() == id }
    }

    fun post(doc: Doc) {
        if (this.docs.any { it.id() == doc.id() }) error("doc is existed, use set to modify")
        docs.add(doc)
        listeners.forEach {
            it.onChanged(doc, Change.Added)
        }
        persistence.save()
    }

    fun set(id: Id, fn: (Doc) -> Doc) {
        val index = this.docs.indexOfFirst { it.id() == id }
        if (index == -1) error("not found")
        val old = this.docs[index]
        val new = fn(old)
        this.docs[index] = new
        listeners.forEach {
            it.onChanged(new, Change.Changed)
        }
        persistence.save()
    }

    fun postOrSet(doc: Doc) {
        val index = this.docs.indexOfFirst { it.id() == doc.id() }
        if (index == -1) {
            docs.add(doc)
            listeners.forEach {
                it.onChanged(doc, Change.Added)
            }
        } else {
            docs[index] = doc
            listeners.forEach {
                it.onChanged(doc, Change.Changed)
            }
        }
        persistence.save()
    }
}




