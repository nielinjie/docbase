package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class HashTest : StringSpec({
    "keys" {
        val doc = BasicDoc(
            "name", "content",
            mapOf("a" to JsonPrimitive("b"))
        )
        doc.getHash().shouldBe(
            BasicDoc(
                "name", "content",
                mapOf("a" to JsonPrimitive("b"))
            ).getHash()
        )
    }
    "keys order" {
        val doc = BasicDoc(
            "name", "content",
            mapOf("a" to JsonPrimitive("b"), "c" to JsonPrimitive("d"))
        )
        doc.getHash().shouldBe(
            BasicDoc(
                "name", "content",
                mapOf("c" to JsonPrimitive("d"), "a" to JsonPrimitive("b"))
            ).getHash()
        )
    }
    "keys order not same" {
        val doc = BasicDoc(
            "name", "content",
            mapOf("a" to JsonPrimitive("b"), "e" to JsonPrimitive("f"), "c" to JsonPrimitive("d"))
        )
        doc.getHash().shouldBe(
            BasicDoc(
                "name", "content",
                mapOf("a" to JsonPrimitive("b"), "c" to JsonPrimitive("d"), "e" to JsonPrimitive("f"))
            ).getHash()
        )
    }
    "nested keys order" {
        val doc = BasicDoc(
            "name", "content",
            mapOf(
                "a" to JsonObject(
                    mapOf(
                        "b" to JsonPrimitive("c"),
                        "d" to JsonPrimitive("e")
                    )
                )
            )
        )
        doc.getHash().shouldBe(
            BasicDoc(
                "name", "content",
                mapOf(
                    "a" to JsonObject(
                        mapOf(
                            "d" to JsonPrimitive("e"),
                            "b" to JsonPrimitive("c")
                        )
                    )
                )
            ).getHash()
        )
    }
})