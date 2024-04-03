package xyz.nietongxue.docbase

import com.appmattus.crypto.Algorithm
import xyz.nietongxue.common.base.Hash


actual fun hashString(s: String): Hash {
    return hashBytes(s.encodeToByteArray())
}

@OptIn(ExperimentalStdlibApi::class)
fun hashBytes(bytes: ByteArray): Hash {
    val digest = Algorithm.MD5.createDigest()
    return digest.digest(bytes).toHexString().let {
        Hash(it)
    }
}