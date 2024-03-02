package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BaseTest : StringSpec({
    "basic" {
        val base = MemoryBase()
        base.post(BasicDoc("name", "content"))
        base.select(docSelector(DocDimension.Phase.matcher("le", "require"))).size shouldBe 0
        base.post(BasicDoc("name2", "content", mapOf(DocDimension.Phase.value("require"))))
        base.select(docSelector(DocDimension.Phase.matcher("le", "require"))).size shouldBe 1
    }
    "serialize" {
        val base = MemoryBase()
        base.post(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))))
        val json = Json.encodeToString(base.docs)
        json.shouldContainInOrder("phase", "require")
        val doc2 = BasicDoc(
            "name2", "content", mapOf(DocDimension.Phase.value("design")),
            docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
        )
        base.post(doc2)
        val json2 = Json.encodeToString(base.docs)
        json2.shouldContainInOrder("phase", "design")
        json2.shouldContainInOrder("phase", "le", "require")
        //TODO 默认的序列化出来有点长。

        val docs = Json.decodeFromString<List<BasicDoc>>(json2)
        (docs shouldHaveSize 2).also {
            (it.last().id()).shouldBe(doc2.id())
        }
    }
    "select" {
        val doc = BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require")))
        val selector = docSelector(DocDimension.Phase.matcher("le", "require"))
        val base = MemoryBase()
        base.post(doc)
        base.select(selector).shouldHaveSize(1).also {
            it.first().id().shouldBe(doc.id())
        }
    }

})