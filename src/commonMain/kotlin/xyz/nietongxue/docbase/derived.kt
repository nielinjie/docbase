package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import xyz.nietongxue.common.base.Attrs
import xyz.nietongxue.common.base.Change
import xyz.nietongxue.common.base.Id
import xyz.nietongxue.common.base.Name
import xyz.nietongxue.common.base.j


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

fun getOriginDocs(derivedDoc: DerivedDoc, base: DefaultBase): List<Doc> {
    return derivedDoc.derived.origins.map { base.get(it) }
}
fun getDerivedDocs(origin: Doc, base: DefaultBase): List<DerivedDoc> {
    return base.docs.filterIsInstance<DerivedDoc>().filter {
        it.derived.origins.contains(origin.id())
    }
}

// segmentsText content
//TODO 目前只考虑文本内容
fun segmentDoc(ref: ReferringDoc, source: Importer, segmentMethods: List<SegmentMethod>): List<DerivedDoc> {
    return segmentMethods.map {
        it.segment(ref, source).map {
            derivedDoc(it.first.let {
                when (it) {
                    is Segment.StringSegment -> it.content
                    else -> error("not support yet")
                }
            }, ref, it.second)
        }
    }.flatten()
}

class SegmentDerivingTrigger(val source: Importer) : Trigger, BaseAware {
    private var base: Base? = null
    private val methods = listOf(SegmentMethod.WholeSegment, SegmentMethod.LineSegment)
    override val sourceInfo: String
        get() = "SegmentDerivingTrigger:"

    override fun updateBase(docBase: DefaultBase) {
        TODO("Not yet implemented")
    }

    override fun onChanged(docChangeEvent: DocChangeEvent) {
        require(this.base != null && this.base is DefaultBase)
        val b: DefaultBase = this.base!! as DefaultBase
        val (doc, change) = docChangeEvent
        if (doc !is ReferringDoc) return
        val ref = docChangeEvent.doc as ReferringDoc
        //find all derived doc from this ref
        val derivedDocs = b.docs.filterIsInstance<DerivedDoc>().filter {
            it.derived.origins.contains(ref.id())
        }
        when (change) {
            Change.Added -> {
                val derived = segmentDoc(ref, source, this.methods)
                derived.forEach {
                    b.post(it)
                }
            }

            Change.Changed -> {
                derivedDocs.forEach {
                    b.delete(it.id())
                }
                val derived = segmentDoc(ref, source, this.methods)
                derived.forEach {
                    b.post(it)
                }
            }

            Change.Removed -> {
                derivedDocs.forEach {
                    b.delete(it.id())
                }
            }
        }
    }

    override fun setBase(base: Base) {
        this.base = base
    }

}