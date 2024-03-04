package xyz.nietongxue.docbase

import com.appmattus.crypto.Algorithm
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import xyz.nietongxue.common.base.Hash


fun <T> hashObject(obj: T, serializer: KSerializer<T>): Hash {
    return Json.encodeToString(serializer, obj).let { hashString(it) }
}

fun toJsonElement(obj: Any): JsonElement {
    return when (obj) {
        is JsonElement -> {
            when (obj) {
                is JsonObject -> obj.keys.sorted().associateWith { obj[it]!! }.let { toJsonElement(it) }
                else -> obj
            }
        }

        is String -> JsonPrimitive(obj)
        is Int -> JsonPrimitive(obj)
        is Long -> JsonPrimitive(obj)
        is Collection<*> -> JsonArray(obj.map { toJsonElement(it!!) })
        //TODO 有可能需要处理key的顺序问题。显然，hash的时候key的顺序需要一致。
        is Map<*, *> -> JsonObject(
            obj.map { it.key.toString() to toJsonElement(it.value!!) }
                .sortedBy { it.first }
                .toMap())

        else -> error("not supported -  $obj")
    }
}

fun hashProperties(vararg property: Pair<String, Any>): Hash {
    return property.map {
        it.first to toJsonElement(it.second)
    }.toMap().let {
        Json.encodeToString(it)
    }.let { hashString(it) }
}


@OptIn(ExperimentalStdlibApi::class)
fun hashString(s: String): Hash {
    val digest = Algorithm.MD5.createDigest()
    return digest.digest(s.encodeToByteArray()).toHexString().let {
        Hash(it)
    }
}