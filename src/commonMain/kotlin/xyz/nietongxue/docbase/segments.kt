package xyz.nietongxue.docbase

interface SegmentMethod {
    fun segment(referringDoc: ReferringDoc): List<Pair<String, Deriving>>

    object LineSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Deriving>> {
            return referringDoc.lines().mapIndexed { index, s ->
                s to Deriving("line-segment", mapOf("lineNo" to index.toString()))
            }
        }
    }

    object ParagraphSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Deriving>> {
            return referringDoc.paragraphs().mapIndexed { index, s ->
                s to Deriving("paragraph-segment", mapOf("paraNo" to index.toString()))
            }
        }
    }

    object PageSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Deriving>> {
            return referringDoc.pages().mapIndexed { index, s ->
                s to Deriving("page-segment", mapOf("pageNo" to index.toString()))
            }
        }
    }

    object ChapterSegment : SegmentMethod {
        override fun segment(referringDoc: ReferringDoc): List<Pair<String, Deriving>> {
            return referringDoc.chapters().mapIndexed { index, s ->
                s to Deriving("chapter-segment", mapOf("chapterNo" to index.toString()))
            }
        }
    }
}