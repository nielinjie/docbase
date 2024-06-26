package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import xyz.nietongxue.common.base.Serializing
import xyz.nietongxue.common.base.globalSerializing
import xyz.nietongxue.common.base.j
import xyz.nietongxue.dev.Phase
import xyz.nietongxue.docbase.depends.DependsDoc
import xyz.nietongxue.docbase.depends.declareDepend


class SerializeTest : StringSpec(
    {
        globalSerializing.plus(SerializersModule {
            polymorphic(Doc::class) {
                subclass(SimpleDoc::class)
                subclass(DependsDoc::class)
            }
            polymorphic(Matcher::class) {
                subclass(DimensionMatcher::class)
            }
            polymorphic(DocDimension::class) {
                subclass(Phase::class)
            }
        })
        "serialize" {
            val base = testingBase()
            base.post(DependsDoc("name", "content", mapOf(Phase.value("require"))))
            val json = j().encodeToString(base.docs)
            json.shouldContainInOrder("phase", "require")
            val doc2 = DependsDoc(
                "name2", "content", mapOf(Phase.value("design")),
                docSelector(Phase.matcher("le", "require")).declareDepend()
            )
            base.post(doc2)
            val json2 = j().encodeToString(base.docs)
            json2.shouldContainInOrder("phase", "design")
            json2.shouldContainInOrder("phase", "le", "require")
            //TODO 默认的序列化出来有点长。
            val docs = j().decodeFromString<List<Doc>>(json2)
            (docs shouldHaveSize 2).also {
                (it.last().id()).shouldBe(doc2.id())
            }
        }
    })