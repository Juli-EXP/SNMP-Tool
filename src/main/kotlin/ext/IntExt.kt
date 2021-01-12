package ext

import java.net.InetAddress

fun Int.toInetAddress(): InetAddress {
    val ipByteArray = byteArrayOf(
        (this shr (8 * 3)).toByte(),
        (this shr (8 * 2)).toByte(),
        (this shr (8 * 1)).toByte(),
        (this shr (8 * 0)).toByte()
    )
    return InetAddress.getByAddress(ipByteArray)
}
