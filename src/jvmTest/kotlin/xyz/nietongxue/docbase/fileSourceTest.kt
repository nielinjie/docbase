package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

val baseDir = File("/Users/nielinjie/Projects/docbaseK")

class FileSourceTest : StringSpec({
    "listAllFiles" {
        val dir = File(baseDir, "fileSourceTest")
        val files = listAllFiles(dir)
        files.size shouldBe 2
    }
    "file source" {
        val dir = File(baseDir, "fileSourceTest")
        val source = FileSystemSource(dir)
        val docs = source.refDocs()
        docs.size shouldBe 2
        docs.first().also {
            it.referring.also {
                it.refPath shouldBe "a.txt"
                it.fileContentHash shouldBe hashBytes(File(dir, "a.txt").readBytes())
                it.sourceInfo shouldBe "fileSystemSource://$dir"
            }
            it.attrs["path"]!!.jsonPrimitive.content shouldBe "fileSystemSource://$dir/a.txt"
            it.name shouldBe "a.txt"
        }
    }
    "base update"{
        val base = DefaultBase()
        val dir = File(baseDir, "fileSourceTest")
        val source = FileSystemSource(dir)
        source.updateBase(base)
        base
//        base.select(docSelector(DocDimension.Path.matcher("eq", "fileSystemSource://$dir/a.txt"))).size shouldBe 1
    }
})