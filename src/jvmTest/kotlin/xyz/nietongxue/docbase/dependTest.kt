package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class DependTest : StringSpec({

    "check dep" {
        val base = MemoryBase()
        base.post(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))))
        val doc = BasicDoc(
            "name2", "content", mapOf(DocDimension.Phase.value("design")),
            (docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().id() .shouldBe(doc.id())
        }
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
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().id() shouldBe doc2.id()
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.set(doc2.id()) {
            it.copy(content = "new content")
        }
        (base.checkDependOutDated() shouldHaveSize 1).also{
            it.first().id() shouldBe doc2.id()
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
    }
})