package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonPrimitive
import xyz.nietongxue.common.base.Change
import java.io.File

fun listeningBase(docListener: DocListener): DefaultBase {
    val base = DefaultBase(DoNothingPersistence, mutableListOf(docListener), mutableListOf())
    return base
}


data class Event(val doc: Doc, val change: Change)
class BaseListeningTest : StringSpec({
    "simple" {
        val events = mutableListOf<Event>()
        val base = listeningBase(object : DocListener {
            override fun onChanged(doc: Doc, change: Change) {
                events.add(Event(doc, change))
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
        val base = DefaultBase(DoNothingPersistence, mutableListOf(), mutableListOf(object : BaseListener {
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
        val events = mutableListOf<Event>()
        val file = File(
            baseDir,
            "fileSourceTest"
        )
        val base = DefaultBase(DoNothingPersistence, mutableListOf(object : DocListener {
            override fun onChanged(doc: Doc, change: Change) {
                events.add(Event(doc, change))
            }
        }), mutableListOf(FileSystemImporter(file)))

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