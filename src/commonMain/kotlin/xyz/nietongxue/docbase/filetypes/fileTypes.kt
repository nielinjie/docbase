package xyz.nietongxue.docbase.filetypes

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.*

val fileTypes: List<FileType> = listOf(Txt(),Md(), Pdf(), Word())


interface FileType {
    fun forPath(path: Path): Boolean
    fun segment(path: String, segmentMethod: SegmentMethod, source: Importer): SegmentResult
}
