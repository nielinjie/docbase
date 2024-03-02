package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SelectorTest : StringSpec({
    "area" {
        val doc = BasicDoc("name", "content", mapOf(DocDimension.Area.value("/base/user")))
        Matcher("area", "in", "/base").match(doc).shouldBe(true)
        Matcher("area", "in", "/ba").match(doc).shouldBe(false)
        Matcher("area", "in", "/base/user").match(doc).shouldBe(false)
        Matcher("area", "inOrEq", "/base/user").match(doc).shouldBe(true)
    }
})