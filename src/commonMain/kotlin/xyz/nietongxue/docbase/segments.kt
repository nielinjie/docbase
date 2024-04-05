package xyz.nietongxue.docbase

interface SegmentMethod {
    fun segment(referringDoc: ReferringDoc): List<Pair<String, Derived>>

    object LineSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Derived>> {
            return referringDoc.lines().mapIndexed { index, s ->
                s to Derived("line-segment", mapOf("lineNo" to index.toString()), listOf())
            }
        }
    }
    object ParagraphSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Derived>> {
            return referringDoc.paragraphs().mapIndexed { index, s ->
                s to Derived("paragraph-segment", mapOf("paraNo" to index.toString()), listOf())
            }
        }
    }
    object PageSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Derived>> {
            return referringDoc.pages().mapIndexed { index, s ->
                s to Derived("page-segment", mapOf("pageNo" to index.toString()), listOf())
            }
        }
    }
    object ChapterSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Derived>> {
            return referringDoc.chapters().mapIndexed { index, s ->
                s to Derived("chapter-segment", mapOf("chapterNo" to index.toString()), listOf())
            }
        }
    }
}