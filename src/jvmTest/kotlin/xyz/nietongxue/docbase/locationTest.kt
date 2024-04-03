package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import xyz.nietongxue.dev.Phase

class LocationTest : StringSpec({
    "basic" {
        val doc = SimpleDoc("name", "content", mapOf(Phase.value("require")))
        doc.getHash()
            .shouldBe(SimpleDoc("name", "content", mapOf(Phase.value("require"))).getHash())
    }

})