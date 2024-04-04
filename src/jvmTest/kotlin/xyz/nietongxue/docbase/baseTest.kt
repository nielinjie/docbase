package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import xyz.nietongxue.dev.Phase


class BaseTest : StringSpec({
    "basic" {
        val base = DefaultBase()
        base.post(SimpleDoc("name", "content"))
        base.select(docSelector(Phase.matcher("le", "require"))).size shouldBe 0
        base.post(SimpleDoc("name2", "content", mapOf(Phase.value("require"))))
        base.select(docSelector(Phase.matcher("le", "require"))).size shouldBe 1
    }

    "select" {
        val doc = SimpleDoc("name", "content", mapOf(Phase.value("require")))
        val selector = docSelector(Phase.matcher("le", "require"))
        val base = DefaultBase()
        base.post(doc)
        base.select(selector).shouldHaveSize(1).also {
            it.first().id().shouldBe(doc.id())
        }
    }


})