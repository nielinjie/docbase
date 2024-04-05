package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
sealed interface DocSelector {
    fun match(doc: Doc): Boolean
}

@Serializable
class DocSelectorAnd(val pres: List<Matcher>) : DocSelector { //所有的都and
    override fun match(doc: Doc): Boolean {
        return pres.all { it.match(doc) }
    }
}

fun docSelector(vararg matchers: Matcher): DocSelector {
    return DocSelectorAnd(matchers.toList())
}

@Serializable
class DocSelectorOr(val selectors: List<DocSelector>) : DocSelector {
    override fun match(doc: Doc): Boolean {
        return selectors.any { it.match(doc) }
    }
}


@Serializable
class DocSelectorNot(val selector: DocSelector) : DocSelector {
    override fun match(doc: Doc): Boolean {
        return !selector.match(doc)
    }
}

interface Matcher {
    fun match(doc: Doc): Boolean
}

@Serializable
class IdMatcher(val id: String) : Matcher {
    override fun match(doc: Doc): Boolean {
        return doc.id() == id
    }
}

@Serializable
class AttrMatcher(val key: String, val value: JsonElement) : Matcher {
    override fun match(doc: Doc): Boolean {
        return doc.attrs[key] == value
    }
}

