package xyz.nietongxue.docbase

import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Change

interface Base

interface Listener


data class DocChangeEvent(val doc: Doc, val change: Change)
interface BaseAware {
    fun setBase(base: Base)
}

interface DocListener : Listener {
    fun onChanged(docChangeEvent: DocChangeEvent)
}

interface BaseListener : Listener {
    fun onOpen(base: Base)
}

open class DefaultBase(
    val persistence: Persistence,
    val listeners: List<Listener>,
) : Base {
    val docs = mutableListOf<Doc>()
    val docListeners = mutableListOf<DocListener>()
    val baseListeners = mutableListOf<BaseListener>()

    init {
        listeners.filterIsInstance<BaseAware>().forEach {
            it.setBase(this)
        }
        baseListeners.also {
            it.clear()
            it.addAll(listeners.filterIsInstance<BaseListener>())
        }
        docListeners.also {
            it.clear()
            it.addAll(listeners.filterIsInstance<DocListener>())
        }
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
        docListeners.forEach {
            it.onChanged(DocChangeEvent(doc, Change.Added))
        }
        persistence.save()
    }

    fun set(id: Id, fn: (Doc) -> Doc) {
        val index = this.docs.indexOfFirst { it.id() == id }
        if (index == -1) error("not found")
        val old = this.docs[index]
        val new = fn(old)
        this.docs[index] = new
        docListeners.forEach {
            it.onChanged(DocChangeEvent(new, Change.Changed))
        }
        persistence.save()
    }

    fun postOrSet(doc: Doc) {
        val index = this.docs.indexOfFirst { it.id() == doc.id() }
        if (index == -1) {
            docs.add(doc)
            docListeners.forEach {
                it.onChanged(DocChangeEvent(doc, Change.Added))
            }
        } else {
            docs[index] = doc
            docListeners.forEach {
                it.onChanged(DocChangeEvent(doc, Change.Changed))
            }
        }
        persistence.save()
    }

    //TODO 需要是逻辑删除不？
    fun delete(id: Id) {
        val index = this.docs.indexOfFirst { it.id() == id }
        if (index == -1) error("not found")
        val doc = this.docs[index]
        this.docs.removeAt(index)
        docListeners.forEach {
            it.onChanged(DocChangeEvent(doc, Change.Removed))
        }
        persistence.save()
    }
}




