package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import xyz.nietongxue.common.base.Attrs
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.base.Serializing.j


@Serializable
data class Derived(
    val type: String, val params: Map<String, String>, val origins: List<Id>
)

@Serializable
data class Deriving(
    val type: String, val params: Map<String, String>
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
        deriving.toDocName(doc.name), content, doc.attrs, Derived(deriving.type, deriving.params, listOf(doc.id()))
    )

}


// segmentsText content
//TODO 目前只考虑文本内容
fun segmentDoc(ref: ReferringDoc, segmentMethod: SegmentMethod, source: Importer): List<DerivedDoc> {
    return segmentMethod.segment(ref, source).map {
        derivedDoc(it.first.let {
            when (it) {
                is Segment.StringSegment -> it.content
                else -> error("not support yet")
            }
        }, ref, it.second)
    }
}
