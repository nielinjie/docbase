package xyz.nietongxue.docbase.filetypes

import arrow.core.Either
import arrow.core.left
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.*

class Md : FileType {
    override fun segment(path: String, segmentMethod: SegmentMethod, source: Importer): SegmentResult {
        when (segmentMethod) {
            SegmentMethod.WholeSegment -> {
                return Either.Right(listOf(Segment.StringSegment(source.raw(path).decodeToString())))
            }
            else -> {
                return NotSupportedSegmentMethod.left()
            }
        }
    }

    override fun forPath(path: Path): Boolean {
        return path.shortName().endsWith(".md")
    }
}
