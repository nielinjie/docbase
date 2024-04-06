package xyz.nietongxue.docbase.filetypes

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.Importer
import xyz.nietongxue.docbase.ReferringDoc
import xyz.nietongxue.docbase.Segment
import xyz.nietongxue.docbase.SegmentMethod

val fileTypes: List<FileType> = listOf(Md(), Pdf(), Word())


interface FileType {
    fun forPath(path: Path): Boolean
    fun segment(path: String, segmentMethod: SegmentMethod, source: Importer): List<Segment>
}
