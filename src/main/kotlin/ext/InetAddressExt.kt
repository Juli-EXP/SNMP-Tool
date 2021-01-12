package ext

import java.net.InetAddress

fun InetAddress.toInt(): Int {
    return ((this.address[0].toInt() and 0xFF) shl (8 * 3)) or
            ((this.address[1].toInt() and 0xFF) shl (8 * 2)) or
            ((this.address[2].toInt() and 0xFF) shl (8 * 1)) or
            ((this.address[3].toInt() and 0xFF) shl (8 * 0))
}

