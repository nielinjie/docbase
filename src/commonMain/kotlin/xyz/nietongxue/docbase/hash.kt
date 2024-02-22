package xyz.nietongxue.docbase

import com.appmattus.crypto.Algorithm
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import xyz.nietongxue.common.base.Hash


fun <T> hashObject(obj: T, serializer: KSerializer<T>): Hash {
    return Json.encodeToString(serializer, obj).let { hashString(it) }
}



@OptIn(ExperimentalStdlibApi::class)
fun hashString(s: String): Hash {
    val digest = Algorithm.MD5.createDigest()
    return digest.digest(s.encodeToByteArray()).toHexString().let {
        Hash(it)
    }
}