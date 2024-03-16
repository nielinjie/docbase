package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldNotBeInstanceOf
import xyz.nietongxue.common.base.Diff

class DependTest : StringSpec({

    "check dep" {
        val base = SimpleBase()
        base.post(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))))
        val doc = BasicDoc(
            "name2",
            "content",
            mapOf(DocDimension.Phase.value("design")),
            (docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id().shouldBe(doc.id())
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        base.update(doc.id())
        base.checkDependOutDated() shouldHaveSize 0
    }
    "add file" {
        val base = SimpleBase()
        base.post(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))))
        val doc = BasicDoc(
            "name2",
            "content",
            mapOf(DocDimension.Phase.value("design")),
            docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
        )
        base.post(doc)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id().shouldBe(doc.id())
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        val docName3 = BasicDoc(
            "name3",
            "content",
            mapOf(DocDimension.Phase.value("design")),
            docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
        )
        base.post(
            docName3
        )
        (base.checkDependOutDated() shouldHaveSize 2).also {
            it.first().first.id().shouldBe(doc.id())
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
            it.last().first.id().shouldBe(docName3.id())
            it.last().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()

        }//name2 和 name3 都是依赖于 name，都是outofdate。
    }
    "add depend file" {
        val base = SimpleBase()
        val docName = BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require")))
        base.post(docName)
        val doc = BasicDoc(
            "name2",
            "content",
            mapOf(DocDimension.Phase.value("design")),
            docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend()
        )
        base.post(doc)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id().shouldBe(doc.id())
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        base.update(doc.id())
        base.checkDependOutDated() shouldHaveSize 0
        val docName1 = BasicDoc(
            "name1", "content", mapOf(DocDimension.Phase.value("require")),
        )
        base.post(
            docName1
        )
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id() shouldBe doc.id()
            it.first().second.shouldBeInstanceOf<DependSatisfied.UnsatisfiedDiffs>().also {
                it.diffs.added.ids.shouldHaveSize(1).also {
                    it.first().shouldBe(docName1.id())
                }
            }
        }
    }
    "change depended" {
        val base = SimpleBase()
        val doc1 = BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require")))
        base.post(doc1)
        val doc2 = BasicDoc(
            "name2",
            "content",
            mapOf(DocDimension.Phase.value("design")),
            (docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc2)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id() shouldBe doc2.id()
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.set(doc1.id()) {
            it.copy(content = "new content")
        }
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id() shouldBe doc2.id()
            it.first().second.shouldBeInstanceOf<DependSatisfied.UnsatisfiedDiffs>().also {
                it.diffs.changed.ids.shouldHaveSize(1).also {
                    it.first() shouldBe doc1.id()
                }
            }
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
    }
    "change itself" {
        val base = SimpleBase()
        val doc1 = BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require")))
        base.post(doc1)
        val doc2 = BasicDoc(
            "name2",
            "content",
            mapOf(DocDimension.Phase.value("design")),
            (docSelector(DocDimension.Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc2)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id() shouldBe doc2.id()
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.set(doc2.id()) {
            it.copy(content = "new content")
        }
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id() shouldBe doc2.id()
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfChanged>()
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
    }
})