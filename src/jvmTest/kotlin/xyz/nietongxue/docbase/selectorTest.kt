package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import xyz.nietongxue.dev.Area

class SelectorTest : StringSpec({
    "area" {
        val doc = SimpleDoc("name", "content", mapOf(Area.value("/base/user")))
        DimensionMatcher(Area, "in", "/base").match(doc).shouldBe(true)
        DimensionMatcher(Area, "in", "/ba").match(doc).shouldBe(false)
        DimensionMatcher(Area, "in", "/base/user").match(doc).shouldBe(false)
        DimensionMatcher(Area, "inOrEq", "/base/user").match(doc).shouldBe(true)
    }
})