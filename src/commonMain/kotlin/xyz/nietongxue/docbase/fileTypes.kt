package xyz.nietongxue.docbase

import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.filetypes.Md
import xyz.nietongxue.docbase.filetypes.Pdf
import xyz.nietongxue.docbase.filetypes.Word

val fileTypes: List<FileType> = listOf(Md(), Pdf(), Word())


interface FileType {
    fun forPath(path: Path): Boolean
    fun segment(path: String, segmentMethod: SegmentMethod, source: Importer): List<Segment>
}

 fun ReferringDoc.segment(segmentMethod: SegmentMethod, source: Importer): List<Segment> {
    val name = Path.fromString(this.referring.refPath)
    val fileType = fileTypes.firstOrNull { it.forPath(name) } ?: error("No file type for $name")
    return fileType.segment(this.referring.refPath, segmentMethod, source)
}