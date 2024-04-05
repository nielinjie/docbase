package xyz.nietongxue.dev

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.common.coordinate.CategoryDimension
import xyz.nietongxue.common.coordinate.OrderedDimension
import xyz.nietongxue.common.coordinate.PathLikeDimension
import xyz.nietongxue.docbase.DocDimension

@Serializable
object Area : DocDimension, PathLikeDimension("area") {
    override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
        val operatedPath = Path.fromString(operated)
        val propertyPath = Path.fromString(propertyValue.jsonPrimitive.content)
        return when (opt) {
            "in" -> (propertyPath).isDescendantOf(operatedPath)
            "inOrEq" -> propertyPath == operatedPath || (propertyPath).isDescendantOf(operatedPath)
            else -> error("unknown opt")
        }
    }
}

@Serializable
object Aspect : DocDimension,
    CategoryDimension("aspect", listOf("entity", "info", "function", "presentation", "page")) {
    override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
        return simpleStringMatch(opt, propertyValue, operated)
    }

    override fun value(value: String): Pair<String, JsonElement> {
        return if (this.categories.contains(value)) this.name to JsonPrimitive(value)
        else error("not a valid category")
    }


}

//TODO version repository
//    object Version : MaterialDimension,
//        OrderedDimension<SingleBaseVersion>("version", VersionSingleStream(emptyList()).toList().toOrdered())
@Serializable
object Layer : DocDimension,
    OrderedDimension("layer", listOf("material", "model", "component", "artifact", "runtime")) {
    override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
        return orderedStringMatch(opt, propertyValue, operated, ordered)
    }

    override fun value(value: String): Pair<String, JsonElement> {
        return if (this.ordered.contains(value)) this.name to JsonPrimitive(value)
        else error("not a valid category")
    }
}

@Serializable
object Phase : DocDimension, OrderedDimension("phase", listOf("require", "design", "develop", "test", "release")) {
    override fun match(propertyValue: JsonElement, opt: String, operated: String): Boolean {
        return orderedStringMatch(opt, propertyValue, operated, ordered)
    }

    override fun value(value: String): Pair<String, JsonElement> {
        return if (this.ordered.contains(value)) this.name to JsonPrimitive(value)
        else error("not a valid category")
    }
}