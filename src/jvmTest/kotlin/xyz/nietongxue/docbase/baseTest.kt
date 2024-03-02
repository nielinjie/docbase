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
        base.post(
            BasicDoc(
                "name2", "content", mapOf(DocDimension.Phase.value("design")),
                docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
            )
        )
        val json2 = Json.encodeToString(base.docs)
        json2.shouldContainInOrder("phase", "design")
        json2.shouldContainInOrder("phase", "le", "require")
        //TODO 默认的序列化出来有点长。

        val docs = Json.decodeFromString<List<BasicDoc>>(json2)
        docs shouldHaveSize 2
//        println(json2)
//        val base2 = Json.decodeFromString(MemoryBase.serializer(), json)
//        base2.select(docSelector(DocDimension.Phase.matcher("le", "require"))).size shouldBe 0
    }
    "check dep" {
        val base = MemoryBase()
        base.post(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))))
        val doc = BasicDoc(
            "name2", "content", mapOf(DocDimension.Phase.value("design")),
            (docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc)
        base.checkDependOutDated() shouldHaveSize 1
        base.update(doc.id())
        base.checkDependOutDated() shouldHaveSize 0
    }
    "add file" {
        val base = MemoryBase()
        base.post(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))))
        val doc = BasicDoc(
            "name2", "content", mapOf(DocDimension.Phase.value("design")),
            docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
        )
        base.post(doc)
        base.checkDependOutDated() shouldHaveSize 1
        base.post(
            BasicDoc(
                "name3", "content", mapOf(DocDimension.Phase.value("design")),
                docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
            )
        )
        base.checkDependOutDated() shouldHaveSize 2 //name2 和 name3 都是依赖于 name，都是outofdate。
    }
    "add depend file" {
        val base = MemoryBase()
        base.post(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))))
        val doc = BasicDoc(
            "name2", "content", mapOf(DocDimension.Phase.value("design")),
            docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
        )
        base.post(doc)
        base.checkDependOutDated() shouldHaveSize 1
        base.update(doc.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.post(
            BasicDoc(
                "name1", "content", mapOf(DocDimension.Phase.value("require")),
            )
        )
        base.checkDependOutDated() shouldHaveSize 1
    }
    "change depended" {
        val base = MemoryBase()
        val doc1 = BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require")))
        base.post(doc1)
        val doc2 = BasicDoc(
            "name2", "content", mapOf(DocDimension.Phase.value("design")),
            (docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc2)
        base.checkDependOutDated() shouldHaveSize 1
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.set(doc1.id()) {
            it.copy(content = "new content")
        }
        base.checkDependOutDated() shouldHaveSize 1
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
    }
    "change itself" {
        val base = MemoryBase()
        val doc1 = BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require")))
        base.post(doc1)
        val doc2 = BasicDoc(
            "name2", "content", mapOf(DocDimension.Phase.value("design")),
            (docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc2)
        base.checkDependOutDated() shouldHaveSize 1
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.set(doc2.id()) {
            it.copy(content = "new content")
        }
        base.checkDependOutDated() shouldHaveSize 1
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
    }
})