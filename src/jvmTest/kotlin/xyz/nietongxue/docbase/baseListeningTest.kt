package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.Change
import java.io.File

fun listeningBase(docListener: DocListener): DefaultBase {
    val base = DefaultBase(DoNothingPersistence,listOf(docListener))
    return base
}


class BaseListeningTest : StringSpec({
    "simple" {
        val events = mutableListOf<DocChangeEvent>()
        val base = listeningBase(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        })
        base.post(SimpleDoc("name", "content"))

        (events.size shouldBe 1)
        events.also {
            it.first().also {
                it.change.shouldBe(Change.Added)
                it.doc.id().shouldBe("name")
            }
        }
        base.set("name") { (it as SimpleDoc).copy(content = "new content") }
        events.shouldHaveSize(2).also {
            it.last().also {
                it.change.shouldBe(Change.Changed)
                it.doc.id().shouldBe("name")
            }
        }
    }
    "base" {
        val baseEvents = mutableListOf<String>()
        val base = DefaultBase(DoNothingPersistence, listOf(object : BaseListener {
            override fun onOpen(base: Base) {
                baseEvents.add("open")
            }
        }))
        base.post(SimpleDoc("name", "content"))
        baseEvents.shouldHaveSize(1).also {
            it.first().shouldBe("open")
        }
    }
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
        }, FileSystemImporter(file)))

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
})