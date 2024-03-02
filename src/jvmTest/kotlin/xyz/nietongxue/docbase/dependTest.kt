package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

class DependTest : StringSpec({
    "select" {
        val doc = BasicDoc("name", "content", mapOf( DocDimension.Phase.value("require")))
        val selector = docSelector(DocDimension.Phase.matcher("le", "require"))
        val base = MemoryBase()
        base.post(doc)
        base.select(selector).shouldHaveSize(1)
    }
})