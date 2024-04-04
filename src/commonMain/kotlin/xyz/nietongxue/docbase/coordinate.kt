package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.common.coordinate.*


interface DocDimension {
    fun simpleStringMatch(
        opt: String, propertyValue: JsonElement, operated: String
    ) = when (opt) {
        "eq" -> propertyValue.jsonPrimitive.content == operated
        "in" -> propertyValue.jsonPrimitive.content in operated.split(",").map { it.trim() }
        else -> error("unknown opt")
    }

    fun orderedStringMatch(
        opt: String, propertyValue: JsonElement, operated: String, ordered: List<String>
    ) = when (opt) {
        "eq" -> propertyValue.jsonPrimitive.content == operated
        "in" -> propertyValue.jsonPrimitive.content in operated.split(",").map { it.trim() }
        "le" -> ordered.indexOf(propertyValue.jsonPrimitive.content) <= ordered.indexOf(operated)
        "lt" -> ordered.indexOf(propertyValue.jsonPrimitive.content) < ordered.indexOf(operated)
        "ge" -> ordered.indexOf(propertyValue.jsonPrimitive.content) >= ordered.indexOf(operated)
        "gt" -> ordered.indexOf(propertyValue.jsonPrimitive.content) > ordered.indexOf(operated)
        else -> error("unknown opt")
    }



    fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean // (prop opt operated) is true?
    fun matcher(opt: String, operated: String): DimensionMatcher {
        return DimensionMatcher(this, opt, operated)
    }

    fun value(value: String): Pair<String, JsonElement> {
        return this.name to JsonPrimitive(value)
    }


    val name: String

}


@Serializable
class DimensionMatcher(val dimension: DocDimension, val opt: String, val value: String):Matcher {
    override fun match(doc: Doc): Boolean {
        val value = doc.attrs[dimension.name]
        return if (value == null) {
            false //TODO 应该是true？没提及就是所有的？
        } else {
            dimension.match(value, opt, this.value)
        }

    }
}
