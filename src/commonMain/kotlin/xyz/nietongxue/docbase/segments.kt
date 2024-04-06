package xyz.nietongxue.docbase


interface Segment{
    class StringSegment(val content: String) : Segment
    class MediaSegment():Segment //TODO
}


interface SegmentMethod {
    fun segment(referringDoc: ReferringDoc,source: Importer): List<Pair<Segment, Deriving>>

    object WholeSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc,source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this,source).also {
                check(it.size == 1) {
                    "WholeSegment should only have one segment, but got ${it.size} segments."
                }
            }.mapIndexed { index, s ->
                s to Deriving("whole-segment", mapOf("whole" to index.toString()))
            }
        }
    }

    object LineSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc,source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this,source).mapIndexed { index, s ->
                s to Deriving("line-segment", mapOf("lineNo" to index.toString()))
            }
        }
    }

    object ParagraphSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc,source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this,source).mapIndexed { index, s ->
                s to Deriving("paragraph-segment", mapOf("paraNo" to index.toString()))
            }
        }
    }

    object PageSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc,source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this,source).mapIndexed { index, s ->
                s to Deriving("page-segment", mapOf("pageNo" to index.toString()))
            }
        }
    }

    object ChapterSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc,source: Importer): List<Pair<Segment, Deriving>> {
            return referringDoc.segment(this,source).mapIndexed { index, s ->
                s to Deriving("chapter-segment", mapOf("chapterNo" to index.toString()))
            }
        }
    }
}