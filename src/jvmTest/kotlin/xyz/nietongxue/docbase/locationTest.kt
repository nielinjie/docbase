package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive

class LocationTest : StringSpec({
    "basic" {
        val doc = BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require")))
        doc.getHash()
            .shouldBe(BasicDoc("name", "content", mapOf(DocDimension.Phase.value("require"))).getHash())
    }

})