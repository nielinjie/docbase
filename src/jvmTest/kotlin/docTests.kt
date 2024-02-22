import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.JsonPrimitive
import xyz.nietongxue.docbase.BasicDoc

class DocTest : StringSpec({
    "basic doc" {
        val doc = BasicDoc("name", "content")
        doc.getHash().shouldBe(BasicDoc("name", "content").getHash())
    }
    "same with attrs" {
        val doc = BasicDoc(
            "name", "content",
            mutableMapOf("a" to JsonPrimitive("b"))
        )
        doc.getHash().shouldBe(
            BasicDoc(
                "name", "content",
                mutableMapOf("a" to JsonPrimitive("b"))
            ).getHash()
        )
    }
    "dont same" {
        val doc = BasicDoc("name", "content")
        doc.getHash().shouldNotBe(BasicDoc("name", "content1").getHash())
    }
    "dont same with attrs" {
        val doc = BasicDoc("name", "content")
        doc.getHash().shouldNotBe(
            BasicDoc(
                "name", "content",
                mutableMapOf("a" to JsonPrimitive("b"))
            ).getHash()
        )
    }
    "id" {
        val doc = BasicDoc("name", "content")
        doc.id().shouldBe("name")
    }
    "id with path" {
        val doc = BasicDoc(
            "name", "content",
            mutableMapOf("path" to JsonPrimitive("a"))
        )
        doc.id().shouldBe("a/name")
        val doc2 = BasicDoc(
            "name", "content",
            mutableMapOf("path" to JsonPrimitive("a/b"))
        )
        doc2.id().shouldBe("a/b/name")
    }
})