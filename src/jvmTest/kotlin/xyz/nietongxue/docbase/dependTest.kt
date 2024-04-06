package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import xyz.nietongxue.common.base.Serializing
import xyz.nietongxue.dev.Phase
import xyz.nietongxue.docbase.depends.DependSatisfied
import xyz.nietongxue.docbase.depends.DependsDoc
import xyz.nietongxue.docbase.depends.declareDepend

class DependTest : StringSpec({
    Serializing.plus(SerializersModule {
        polymorphic(Matcher::class) {
            subclass(DimensionMatcher::class)
        }
        polymorphic(DocDimension::class) {
            subclass(Phase::class)
        }
    })
    "depend declare" {

        val doc = DependsDoc(
            "name", "content",
            declare = (docSelector(Phase.matcher("le", "require")).declareDepend())
        )
        doc.getHash().shouldBe(
            DependsDoc(
                "name", "content",
                declare = (docSelector(Phase.matcher("le", "require")).declareDepend())
            ).getHash()
        )
    }
    "check dep" {
        val base = testingDependBase()
        base.post(DependsDoc("name", "content", mapOf(Phase.value("require"))))
        val doc = DependsDoc(
            "name2",
            "content",
            mapOf(Phase.value("design")),
            (docSelector(Phase.matcher("le", "require")).declareDepend())
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
        val base = testingDependBase()
        base.post(DependsDoc("name", "content", mapOf(Phase.value("require"))))
        val doc = DependsDoc(
            "name2",
            "content",
            mapOf(Phase.value("design")),
            docSelector(Phase.matcher("le", "require")).declareDepend()
        )
        base.post(doc)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id().shouldBe(doc.id())
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        val docName3 = DependsDoc(
            "name3",
            "content",
            mapOf(Phase.value("design")),
            docSelector(Phase.matcher("le", "require")).declareDepend()
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
        val base = testingDependBase()
        val docName = DependsDoc("name", "content", mapOf(Phase.value("require")))
        base.post(docName)
        val doc = DependsDoc(
            "name2",
            "content",
            mapOf(Phase.value("design")),
            docSelector(Phase.matcher("le", "require")).declareDepend()
        )
        base.post(doc)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id().shouldBe(doc.id())
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        base.update(doc.id())
        base.checkDependOutDated() shouldHaveSize 0
        val docName1 = DependsDoc(
            "name1", "content", mapOf(Phase.value("require")),
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
        val base = testingDependBase()
        val doc1 = DependsDoc("name", "content", mapOf(Phase.value("require")))
        base.post(doc1)
        val doc2 = DependsDoc(
            "name2",
            "content",
            mapOf(Phase.value("design")),
            (docSelector(Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc2)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id() shouldBe doc2.id()
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.set(doc1.id()) {
            require(it is DependsDoc)
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
        val base = testingDependBase()
        val doc1 = DependsDoc("name", "content", mapOf(Phase.value("require")))
        base.post(doc1)
        val doc2 = DependsDoc(
            "name2",
            "content",
            mapOf(Phase.value("design")),
            (docSelector(Phase.matcher("le", "require")).declareDepend())
        )
        base.post(doc2)
        (base.checkDependOutDated() shouldHaveSize 1).also {
            it.first().first.id() shouldBe doc2.id()
            it.first().second.shouldBeInstanceOf<DependSatisfied.SelfIsNew>()
        }
        base.update(doc2.id())
        base.checkDependOutDated() shouldHaveSize 0
        base.set(doc2.id()) {
            require(it is DependsDoc)
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