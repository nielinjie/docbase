package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.Attrs
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.SerializerM.j


@Serializable
data class Derived(
    val type: String,
    val params: Map<String, String>,
    val origins: List<String>
)

@Serializable
data class DerivedDoc(
    override val name: String,
    override val content: String,
    override val attrs: Attrs<JsonElement> = mutableMapOf(),
    val derived: Derived
) : Doc {


    override fun hashProps(): List<Pair<String, JsonElement>> {
        return super.hashProps() + ("derived" to j().encodeToJsonElement(derived))
    }

}

fun Map<String, String>.pairedString(): String {
    return this.map { "${it.key}_${it.value}" }.joinToString("_")

}

fun Derived.toDocName(originName: String): String {
    return "$originName-${type}-${params.pairedString()}"
}

fun <T> List<T>.ensure(value: T): List<T> {
    return if (this.contains(value)) this else this + value
}

fun derivedDoc(content: String, doc: Doc, derived: Derived): DerivedDoc {
    return (
            derived.copy(origins = derived.origins.ensure(doc.id()))).let {
        DerivedDoc(
            derived.toDocName(doc.name),
            content,
            doc.attrs,
            it
        )
    }
}


// pureText content
fun pureTextDoc(ref: ReferringDoc): Doc {
    return derivedDoc(ref.text(), ref, Derived("pureText", mapOf(), listOf(ref.id())))
}

// segmentsText content
fun segmentDoc(ref: ReferringDoc, segmentMethod: SegmentMethod): List<DerivedDoc> {
    return segmentMethod.segment(ref).map {
        derivedDoc(it.first, ref, it.second)
    }
}
