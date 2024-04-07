package xyz.nietongxue.docbase.filetypes

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.Importer
import xyz.nietongxue.docbase.Segment
import xyz.nietongxue.docbase.SegmentMethod
import xyz.nietongxue.docbase.SegmentResult

class Word :FileType{
    override fun segment(path:String, segmentMethod: SegmentMethod, source: Importer): SegmentResult {
        TODO()
    }

    override fun forPath(path: Path): Boolean {
        return path.shortName().endsWith(".docx")
    }
}