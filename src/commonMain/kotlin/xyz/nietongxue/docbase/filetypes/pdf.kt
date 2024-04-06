package xyz.nietongxue.docbase.filetypes

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.Importer
import xyz.nietongxue.docbase.Segment
import xyz.nietongxue.docbase.SegmentMethod

class Pdf :FileType{
    override fun segment(path:String, segmentMethod: SegmentMethod, source: Importer): List<Segment> {
        TODO()
    }

    override fun forPath(path: Path): Boolean {
        return path.shortName().endsWith(".pdf")
    }
}