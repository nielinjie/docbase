package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray

class PersistenceTest : StringSpec({
    "basic" {
        val jsonStore = JsonStore()
        val persistence = PersistenceJson(jsonStore)
        val base = DefaultBase(persistence)
        base.post(SimpleDoc("name", "content"))
        jsonStore.json.jsonArray.size shouldBe 1
    }
    "born again" {
        val jsonStore = JsonStore()
        val persistence = PersistenceJson(jsonStore)
        val base = DefaultBase(persistence)
        base.post(SimpleDoc("name", "content"))
        val base2 = DefaultBase(persistence)
        base2.docs.also {
            it.size shouldBe 1
            it.first().id().shouldBe(base.docs.first().id())
            it.first().shouldBe(base.docs.first())
        }
    }
})