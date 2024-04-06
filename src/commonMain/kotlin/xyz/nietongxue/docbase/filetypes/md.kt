package xyz.nietongxue.docbase.filetypes

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.Importer
import xyz.nietongxue.docbase.FileType
import xyz.nietongxue.docbase.Segment
import xyz.nietongxue.docbase.SegmentMethod

class Md : FileType {
    override fun segment(path: String, segmentMethod: SegmentMethod, source: Importer): List<Segment> {
        when (segmentMethod) {
            SegmentMethod.WholeSegment -> {
                return listOf(Segment.StringSegment(source.raw(path).decodeToString()))
            }
            else -> {
                TODO()
            }
        }
    }

    override fun forPath(path: Path): Boolean {
        return path.shortName().endsWith(".md")
    }
}
