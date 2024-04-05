package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import xyz.nietongxue.common.base.Attrs
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Name
import xyz.nietongxue.docbase.SerializerM.j


@Serializable
data class Derived(
    val type: String,
    val params: Map<String, String>,
    val origins: List<Id>
)

@Serializable
data class Deriving(
    val type: String,
    val params: Map<String, String>
)

@Serializable
data class DerivedDoc(
    override val name: Name,
    override val content: String,
    override val attrs: Attrs<JsonElement> = mutableMapOf(),
    val derived: Derived
) : Doc {
    override fun hashProps(): List<Pair<String, JsonElement>> {
        return super.hashProps() + ("derived" to j().encodeToJsonElement(derived))
    }

}


fun derivedDoc(content: String, doc: Doc, deriving: Deriving): DerivedDoc {
    fun Map<String, String>.pairedString(): String {
        return this.map { "${it.key}_${it.value}" }.joinToString("_")
    }

    fun Deriving.toDocName(originName: String): String {
        return "$originName-${type}-${params.pairedString()}"
    }

    return DerivedDoc(
        deriving.toDocName(doc.name),
        content,
        doc.attrs,
        Derived(deriving.type, deriving.params, listOf(doc.id()))
    )

}


// pureText content
fun pureTextDoc(ref: ReferringDoc): Doc {
    return derivedDoc(ref.text(), ref, Deriving("pureText", mapOf()))
}

// segmentsText content
fun segmentDoc(ref: ReferringDoc, segmentMethod: SegmentMethod): List<DerivedDoc> {
    return segmentMethod.segment(ref).map {
        derivedDoc(it.first, ref, it.second)
    }
}
