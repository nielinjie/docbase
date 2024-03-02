package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.coordinate.*


interface DocDimension {
    fun simpleStringMatch(
        opt: String,
        propertyValue: JsonElement,
        operated: String
    ) = when (opt) {
        "eq" -> propertyValue.jsonPrimitive.content == operated
        "in" -> propertyValue.jsonPrimitive.content in operated.split(",").map { it.trim() }
        else -> error("unknown opt")
    }

    fun orderedStringMatch(
        opt: String,
        propertyValue: JsonElement,
        operated: String,
        ordered: List<String>
    ) = when (opt) {
        "eq" -> propertyValue.jsonPrimitive.content == operated
        "in" -> propertyValue.jsonPrimitive.content in operated.split(",").map { it.trim() }
        "le" -> ordered.indexOf(propertyValue.jsonPrimitive.content) <= ordered.indexOf(operated)
        "lt" -> ordered.indexOf(propertyValue.jsonPrimitive.content) < ordered.indexOf(operated)
        "ge" -> ordered.indexOf(propertyValue.jsonPrimitive.content) >= ordered.indexOf(operated)
        "gt" -> ordered.indexOf(propertyValue.jsonPrimitive.content) > ordered.indexOf(operated)
        else -> error("unknown opt")
    }

    object Area : DocDimension, PathLikeDimension("area") {
        override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
            return when (opt) {
                "in" -> TODO()
                else -> error("unknown opt")
            }
        }
    }

    object Aspect : DocDimension,
        CategoryDimension("aspect", listOf("entity", "info", "function", "presentation", "page")) {
        override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
            return simpleStringMatch(opt, propertyValue, operated)
        }

        override fun value(value: String): Pair<String, JsonElement> {
            return if (this.categories.contains(value))
                this.name to JsonPrimitive(value)
            else
                error("not a valid category")
        }


    }

    //TODO version repository
//    object Version : MaterialDimension,
//        OrderedDimension<SingleBaseVersion>("version", VersionSingleStream(emptyList()).toList().toOrdered())
    object Layer : DocDimension,
        OrderedDimension("layer", listOf("material", "model", "component", "artifact", "runtime")) {
        override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
            return orderedStringMatch(opt, propertyValue, operated, ordered)
        }

        override fun value(value: String): Pair<String, JsonElement> {
            return if (this.ordered.contains(value))
                this.name to JsonPrimitive(value)
            else
                error("not a valid category")
        }
    }

    object Phase : DocDimension,
        OrderedDimension("phase", listOf("require", "design", "develop", "test", "release")) {
        override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
            return orderedStringMatch(opt, propertyValue, operated, ordered)
        }

        override fun value(value: String): Pair<String, JsonElement> {
            return if (this.ordered.contains(value))
                this.name to JsonPrimitive(value)
            else
                error("not a valid category")
        }
    }

    fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean // (prop opt operated) is true?
    fun matcher(opt: String, operated: String): Matcher {
        return Matcher(this.name, opt, operated)
    }

    fun value(value: String): Pair<String, JsonElement> {
        return this.name to JsonPrimitive(value)
    }


    val name: String

}

fun fromDimensionName(string: String): DocDimension {
    return when (string) {
        "area" -> DocDimension.Area
        "aspect" -> DocDimension.Aspect
        "layer" -> DocDimension.Layer
        "phase" -> DocDimension.Phase
        else -> error("unknown dimension")
    }
}

@Serializable
class Matcher(val dimensionName: String, val opt: String, val value: String) {
    fun match(doc: BasicDoc): Boolean {
        val dimension = fromDimensionName(dimensionName)
        val value = doc.attrs[dimensionName]
        return if (value == null) {
            false //TODO 应该是true？没提及就是所有的？
        } else {
            dimension.match(value, opt, this.value)
        }

    }
}

@Serializable
sealed interface DocSelector {
    fun match(doc: BasicDoc): Boolean
}

@Serializable
class DocSelectorAnd(val pres: List<Matcher>) : DocSelector { //所有的都and
    override fun match(doc: BasicDoc): Boolean {
        return pres.all { it.match(doc) }
    }
}

fun docSelector(vararg matchers: Matcher): DocSelector {
    return DocSelectorAnd(matchers.toList())
}

@Serializable
class DocSelectorOr(val selectors: List<DocSelector>) : DocSelector {
    override fun match(doc: BasicDoc): Boolean {
        return selectors.any { it.match(doc) }
    }
}


@Serializable
class DocSelectorNot(val selector: DocSelector) : DocSelector {
    override fun match(doc: BasicDoc): Boolean {
        return !selector.match(doc)
    }
}






