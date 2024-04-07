package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.Change
import java.io.File

class ImporterTest : StringSpec({
    "importer" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(
            baseDir,
            "fileSourceTest"
        )
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, (FileSystemImporter(file))))

        events.shouldHaveSize(2).also {
            it.first().also {
                it.change.shouldBe(Change.Added)
                it.doc.name.shouldBe("a.txt")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
            }
        }
        base.docs.shouldHaveSize(2)
        base.docs.first().also {
            it.name.shouldBe("a.txt")
            it.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
        }
        base.docs.last().also {
            it.name.shouldBe("c.txt")
            it.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file/b"
            it.id().shouldBe("fileSystemSource:$file/b/c.txt")
        }
    }
    "again" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(
            baseDir,
            "fileSourceTest"
        )
        val importer = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, (importer)))
        base.docs.shouldHaveSize(2)
        val newFile = File(file, "d.md")
        newFile.writeText("i am d.")
        importer.updateBase(base)
        base.docs.shouldHaveSize(3)
        events.shouldHaveSize(3).also {
            it.last().also {
                it.change.shouldBe(Change.Added)
                it.doc.name.shouldBe("d.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/d.md")
            }
        }
        newFile.delete()
    }
    "will not duplicated update"{
        val events = mutableListOf<DocChangeEvent>()
        val file = File(
            baseDir,
            "fileSourceTest"
        )
        val importer = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, (importer)))
        base.docs.shouldHaveSize(2)
        val newFile = File(file, "d.md")
        newFile.writeText("i am d.")
        importer.updateBase(base)
        base.docs.shouldHaveSize(3)
        events.shouldHaveSize(3).also {
            it.last().also {
                it.change.shouldBe(Change.Added)
                it.doc.name.shouldBe("d.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/d.md")
            }
        }
        importer.updateBase(base)
        base.docs.shouldHaveSize(3)
        events.shouldHaveSize(3).also {
            it.last().also {
                it.change.shouldBe(Change.Added)
                it.doc.name.shouldBe("d.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/d.md")
            }
        }
        newFile.delete()
    }
    "remove from out" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(
            baseDir,
            "fileSourceTest"
        )
        val importer = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, (importer)))
        base.docs.shouldHaveSize(2)
        val newFile = File(file, "d.md")
        newFile.writeText("i am d.")
        importer.updateBase(base)
        base.docs.shouldHaveSize(3)
        events.shouldHaveSize(3).also {
            it.last().also {
                it.change.shouldBe(Change.Added)
                it.doc.name.shouldBe("d.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/d.md")
            }
        }
        newFile.delete()
        importer.updateBase(base)
        base.docs.shouldHaveSize(2)
        events.shouldHaveSize(4).also {
            it.last().also {
                it.change.shouldBe(Change.Removed)
                it.doc.name.shouldBe("d.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/d.md")
            }
        }
    }
    "rename is add and remove?" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(
            baseDir,
            "fileSourceTest"
        )
        val importer = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, (importer)))
        base.docs.shouldHaveSize(2)
        val newFile = File(file, "d.md")
        newFile.writeText("i am d.")
        importer.updateBase(base)
        base.docs.shouldHaveSize(3)
        events.shouldHaveSize(3).also {
            it.last().also {
                it.change.shouldBe(Change.Added)
                it.doc.name.shouldBe("d.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/d.md")
            }
        }
        val newFile2 = File(file, "e.md")
        newFile.renameTo(newFile2)
        importer.updateBase(base)
        base.docs.shouldHaveSize(3)
        events.shouldHaveSize(5).also {

            it.last().also {
                it.change.shouldBe(Change.Removed)
                it.doc.name.shouldBe("d.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/d.md")
            }
            (it as List<DocChangeEvent>).dropLast(1).last().also {
                it.change.shouldBe(Change.Added)
                it.doc.name.shouldBe("e.md")
                it.doc.attrs["path"]?.jsonPrimitive?.content shouldBe "fileSystemSource:$file"
                it.doc.id().shouldBe("fileSystemSource:$file/e.md")
            }
        }
        newFile2.delete()
    }
})