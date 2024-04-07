package xyz.nietongxue.docbase

import arrow.core.Either
import arrow.core.getOrElse
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.filetypes.fileTypes


interface Segment {
    class StringSegment(val content: String) : Segment
    class MediaSegment() : Segment //TODO
}


interface SegmentMethod {
    fun segment(referringDoc: ReferringDoc, source: Importer): List<Pair<Segment, Deriving>>

    object WholeSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc, source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this, source).map {
                check(it.size == 1) {
                    "WholeSegment should only have one segment, but got ${it.size} segments."
                }
                it.mapIndexed { index, s ->
                    s to Deriving("whole-segment", mapOf("whole" to index.toString()))
                }
            }.getOrElse { emptyList() }
        }
    }

    object LineSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc, source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this, source).map {
                it.mapIndexed { index, s ->
                    s to Deriving("line-segment", mapOf("lineNo" to index.toString()))
                }
            }.getOrElse { emptyList() }
        }
    }

    object ParagraphSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc, source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this, source).map {
                it.mapIndexed { index, s ->
                    s to Deriving("paragraph-segment", mapOf("paraNo" to index.toString()))
                }
            }.getOrElse { emptyList() }
        }
    }

    object PageSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc, source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this, source).map {
                it.mapIndexed { index, s ->
                    s to Deriving("page-segment", mapOf("pageNo" to index.toString()))
                }
            }.getOrElse { emptyList() }
        }
    }

    object ChapterSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc, source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this, source).map {
                it.mapIndexed { index, s ->
                    s to Deriving("chapter-segment", mapOf("chapterNo" to index.toString()))
                }
            }.getOrElse { emptyList() }
        }
    }
}

object NotSupportedSegmentMethod
typealias SegmentResult = Either<NotSupportedSegmentMethod, List<Segment>>

fun ReferringDoc.segment(segmentMethod: SegmentMethod, source: Importer): SegmentResult {
    val name = Path.fromString(this.referring.refPath)
    val fileType = fileTypes.firstOrNull { it.forPath(name) } ?: error("No file type for $name")
    return fileType.segment(this.referring.refPath, segmentMethod, source)
}