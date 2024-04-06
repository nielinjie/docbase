package xyz.nietongxue.docbase

import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import xyz.nietongxue.common.base.Serializing
import xyz.nietongxue.common.base.Serializing.j
import xyz.nietongxue.dev.Phase
import xyz.nietongxue.docbase.depends.DependsDoc

class JsonStore(var json: JsonElement = JsonArray(listOf())) {
}


class PersistenceJson(val store: JsonStore) : Persistence {
    var base_: DefaultBase? = null

    init {
        Serializing.plus(SerializersModule {
            polymorphic(Doc::class) {
                subclass(SimpleDoc::class)
                subclass(DependsDoc::class)
            }
            polymorphic(Matcher::class) {
                subclass(DimensionMatcher::class)
            }
            polymorphic(DocDimension::class) {
                subclass(Phase::class)
            }
        })
    }

    override fun save() {
        base_?.also {
            j().encodeToJsonElement(it.docs).also {
                store.json = it
            }
        }
    }

    override fun load() {
        base_?.also {
            it.docs.clear()
            it.docs.addAll(j().decodeFromJsonElement(store.json))
        }
    }
}

interface Persistence {
    fun save()
    fun load()
    fun setBase(base: DefaultBase) {
        (this as? PersistenceJson)?.base_ = base
    }
}

object DoNothingPersistence : Persistence {
    override fun save() {
        //do nothing
    }

    override fun load() {
        //do nothing
    }
}