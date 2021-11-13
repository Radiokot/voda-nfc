package ua.com.radiokot.voda.extensions

fun ByteArray.encodeHex(): String {
    return joinToString("") { it.toInt().and(0xff).toString(16).padStart(2, '0') }
}

fun String.decodeHex(): ByteArray {
    require(length % 2 == 0) { "Source string must have an even length" }

    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}