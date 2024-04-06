package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.hashBytes
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
        val source = FileSystemImporter(dir)
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
    "base update" {
        val base = testingBase()
        val dir = File(baseDir, "fileSourceTest")
        val source = FileSystemImporter(dir)
        source.updateBase(base)
        (base.docs shouldHaveSize 2).also {
            it.first().also {
                it.shouldBeInstanceOf<ReferringDoc>()
                it.name shouldBe "a.txt"
                it.referring.also {
                    it.refPath shouldBe "a.txt"
                    it.fileContentHash shouldBe hashBytes(File(dir, "a.txt").readBytes())
                    it.sourceInfo shouldBe "fileSystemSource://$dir"
                }
            }
        }
    }
    "base update with same file" {
        val base = testingBase()
        val dir = File(baseDir, "fileSourceTest")
        val source = FileSystemImporter(dir)
        source.updateBase(base)
        source.updateBase(base)
        (base.docs shouldHaveSize 2).also {
            it.first().also {
                it.shouldBeInstanceOf<ReferringDoc>()
                it.name shouldBe "a.txt"
                it.referring.also {
                    it.refPath shouldBe "a.txt"
                    it.fileContentHash shouldBe hashBytes(File(dir, "a.txt").readBytes())
                    it.sourceInfo shouldBe "fileSystemSource://$dir"
                }
            }
        }
    }
    "base update with different file" {
        val base = testingBase()
        val dir = File(baseDir, "fileSourceTest")
        val source = FileSystemImporter(dir)
        source.updateBase(base)
        val oldContent = File(dir, "a.txt").readText()
        File(dir, "a.txt").writeText("new content")
        source.updateBase(base)
        (base.docs shouldHaveSize 2).also {
            it.first().also {
                it.shouldBeInstanceOf<ReferringDoc>()
                it.name shouldBe "a.txt"
                it.referring.also {
                    it.refPath shouldBe "a.txt"
                    it.fileContentHash shouldBe hashBytes(File(dir, "a.txt").readBytes())
                    it.sourceInfo shouldBe "fileSystemSource://$dir"
                }
            }
        }
        File(dir, "a.txt").writeText(oldContent)
    }
    "base update with new file" {
        val base = testingBase()
        val dir = File(baseDir, "fileSourceTest")
        val source = FileSystemImporter(dir)
        source.updateBase(base)
        File(dir, "b.txt").writeText("new content")
        source.updateBase(base)
        (base.docs shouldHaveSize 3).also {
            it.last().also {
                it.shouldBeInstanceOf<ReferringDoc>()
                it.name shouldBe "b.txt"
                it.referring.also {
                    it.refPath shouldBe "b.txt"
                    it.fileContentHash shouldBe hashBytes(File(dir, "b.txt").readBytes())
                    it.sourceInfo shouldBe "fileSystemSource://$dir"
                }
            }
        }
        File(dir, "b.txt").delete()
    }
})