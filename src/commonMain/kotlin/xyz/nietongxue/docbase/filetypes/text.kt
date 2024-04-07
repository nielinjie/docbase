package xyz.nietongxue.docbase.filetypes


import arrow.core.Either
import arrow.core.right
import xyz.nietongxue.common.base.Path
import xyz.nietongxue.docbase.*

class Txt : FileType {
    override fun segment(path: String, segmentMethod: SegmentMethod, source: Importer): SegmentResult {
        when (segmentMethod) {
            SegmentMethod.WholeSegment -> {
                return Either.Right(listOf(Segment.StringSegment(source.raw(path).decodeToString())))
            }

            SegmentMethod.LineSegment -> {
                return Either.Right(source.raw(path).decodeToString().lines().map { Segment.StringSegment(it) })
            }

            else -> {
                return Either.Left(NotSupportedSegmentMethod)
            }
        }
    }

    override fun forPath(path: Path): Boolean {
        return path.shortName().endsWith(".txt")
    }
}
