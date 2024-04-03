package xyz.nietongxue.docbase


import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.JsonPrimitive

class DocTest : StringSpec({
    "basic doc" {
        val doc = SimpleDoc("name", "content")
        doc.getHash().shouldBe(SimpleDoc("name", "content").getHash())
    }
    "same with attrs" {
        val doc = SimpleDoc(
            "name", "content",
            mutableMapOf("a" to JsonPrimitive("b"))
        )
        doc.getHash().shouldBe(
            SimpleDoc(
                "name", "content",
                mutableMapOf("a" to JsonPrimitive("b"))
            ).getHash()
        )
    }
    "dont same" {
        val doc = SimpleDoc("name", "content")
        doc.getHash().shouldNotBe(SimpleDoc("name", "content1").getHash())
    }
    "dont same with attrs" {
        val doc = SimpleDoc("name", "content")
        doc.getHash().shouldNotBe(
            SimpleDoc(
                "name", "content",
                mutableMapOf("a" to JsonPrimitive("b"))
            ).getHash()
        )
    }

    "id" {
        val doc = SimpleDoc("name", "content")
        doc.id().shouldBe("name")
    }
    "id with path" {
        val doc = SimpleDoc(
            "name", "content",
            mutableMapOf("path" to JsonPrimitive("a"))
        )
        doc.id().shouldBe("a/name")
        val doc2 = SimpleDoc(
            "name", "content",
            mutableMapOf("path" to JsonPrimitive("a/b"))
        )
        doc2.id().shouldBe("a/b/name")
    }
})