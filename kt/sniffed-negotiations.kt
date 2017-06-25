
import java.io.File
import java.util.*

private class Chunk(val data: ByteArray, _operation: Byte) {
    val operation = when (_operation) {
        "<".toByteArray()[0] -> "write"
        ">".toByteArray()[0] -> "read"
        else -> "unknown"
    }
}

fun main(args: Array<String>) {
    val decoded = decodeLog("/Users/jetbrains/Desktop/log-sender.txt")
    writeDecodedLog(decoded, "server_read_log.txt")
}


private fun decodeLog(path: String): List<Chunk> {

    val leftBound = "\n|".toByteArray()
    val rightBound = "|\n".toByteArray()

    val bytes = File(path).readBytes()

    val result = ArrayList<Chunk>()
    var readPos = 0

    var len = 0
    var operation = 0.toByte()
    val buf = ByteArray(bytes.size)

    while (readPos < bytes.size) {

        if (bytes[readPos] == leftBound[0] && bytes[readPos + 2] == leftBound[1]) {
            operation = bytes[readPos + 1]
            readPos += 3
            continue
        }

        if (bytes[readPos] == rightBound[0] && bytes[readPos + 1] == rightBound[1]) {
            result += Chunk(buf.slice(0..len - 1).toByteArray(), operation)
            len = 0
            readPos += 2
            continue
        }

        buf[len++] = bytes[readPos++]
    }
    return result
}

private fun writeDecodedLog(log: List<Chunk>, path: String) {
    val file = File(path)
    log.forEach {
        file.appendText("${it.operation}: ${Arrays.toString(it.data)}\n")
    }
}
