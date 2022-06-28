import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File

fun main(args: Array<String>) {
    val input = args.firstOrNull() ?: "C:\\Users\\admin\\Downloads\\513_2022062700_274864917_0012120627 (1).log"
    File("${input}.1").apply {
        if (!exists()) createNewFile()
        bufferedWriter().use { writer ->
            File(input).forEachLine {
                val input: InputLine = Json.decodeFromString(it)
                writer.write(OutputLine.valueOf(input).toString())
                writer.write("\n")
            }
        }
    }
}

data class OutputLine(
    val time: String,
    val tag: String,
    val thread: String,
    val message: String,
    val level: String
) {

    override fun toString(): String {
        return "$time $thread [$level/$tag] $message"
    }

    companion object {

        private const val TEMPLATE = "([A-Z])/Fridge-(.+)：\\[ \\(.+:\\d+\\)#.+ \\] \\[ \\(.+:\\d+\\)#.+ \\]"

        fun valueOf(input: InputLine): OutputLine? {
            try {
                val heads = input.c?.let { TEMPLATE.toRegex().find(it) }?.groupValues
                return OutputLine(
                    time = input.l?.substring(startIndex = 5) ?: "",
                    tag = heads?.getOrNull(2) ?: "",
                    thread = input.n ?: "",
                    level = heads?.getOrNull(1) ?: "",
                    message = input.c?.replace(heads?.getOrNull(0) ?: "", "") ?: ""
                )
            } catch (e: Exception) {
                return null
            }
        }
    }
}

/**
 * example:
 * {
 *     "c":"D/Fridge-GlobalWifiManager：[ (logUtil.java:17)#d ] [ (GlobalWifiManager.java:236)#onReceive ] android.net.wifi.SCAN_RESULTS",
 *     "f":4,
 *     "l":"2022-06-27 00:00:11.186",
 *     "n":"MonitorThread",
 *     "i":150,
 *     "m":false
 * }
 */
@kotlinx.serialization.Serializable
data class InputLine(
    @SerialName("c")
    val c: String?,
    @SerialName("f")
    val f: Int?,
    @SerialName("l")
    val l: String?,
    @SerialName("n")
    val n: String?,
    @SerialName("i")
    val i: Int?,
    @SerialName("m")
    val m: Boolean?
)