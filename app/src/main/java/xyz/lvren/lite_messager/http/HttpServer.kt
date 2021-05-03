package xyz.lvren.lite_messager.http

import android.content.Context
import android.widget.Toast
import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.NanoHTTPD
import org.nanohttpd.protocols.http.response.Response
import org.nanohttpd.protocols.http.response.Response.newChunkedResponse
import org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse
import org.nanohttpd.protocols.http.response.Status
import xyz.lvren.lite_messager.MyApplication
import xyz.lvren.lite_messager.entity.DataSource
import xyz.lvren.lite_messager.entity.FileInfo
import xyz.lvren.lite_messager.entity.Message
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.set

class HttpServer(private val context: Context, private val port: Int) : NanoHTTPD(port) {

    private val controllerMap = LinkedHashMap<String, (IHTTPSession) -> Response>()
    private val fileUpload = FileUpload(context)


    init {
        controllerMap["push"] = this::pushMessage
        controllerMap["startFileUpload"] = this::startFileUpload
        controllerMap["fileUpload"] = this::fileUpload
    }

    override fun serve(session: IHTTPSession?): Response {
        val uri = session!!.uri.removePrefix("/").ifEmpty { "index.html" }
        println("Loading $uri")
        try {
            if (checkUrlIsController(uri)) {
                return dispatcher(uri, session)
            }
            val mime = when (uri.substringAfterLast(".")) {
                "ico" -> "image/x-icon"
                "css" -> "text/css"
                "htm" -> "text/html"
                "html" -> "text/html"
                else -> "application/javascript"
            }

            return newChunkedResponse(
                Status.OK,
                mime,
                context.assets.open("www/$uri") // prefix with www because your files are not in the root folder in assets
            )
        } catch (e: Exception) {
            val message = "Failed to load asset $uri because $e"
            println(message)
            e.printStackTrace()
            return newFixedLengthResponse(message)
        }
    }

    // 检查url是否为controller
    fun checkUrlIsController(url: String): Boolean {
        val keys = controllerMap.keys
        if (url in keys) {
            return true
        }
        return false
    }

    private fun dispatcher(url: String, session: IHTTPSession): Response {
        val controllerFunction = controllerMap[url]
        return if (controllerFunction != null) {
            controllerFunction(session)
        } else {
            newChunkedResponse(
                Status.NOT_FOUND,
                "text/html",
                null // prefix with www because your files are not in the root folder in assets
            )
        }
    }

    private fun pushMessage(session: IHTTPSession): Response {
        val params = session.parms
        val message = params["message"]
        if (message != null) {
            DataSource.messages.add(Message(message))
            println("添加了：${message}")
        }
        return newFixedLengthResponse("hello")
    }

    private fun startFileUpload(session: IHTTPSession): Response {
        if (!fileUpload.isWritable) {
            Toast.makeText(MyApplication.context, "获取不到设备存储，无法传输文件！", Toast.LENGTH_SHORT).show()
            return newFixedLengthResponse("error")
        }
        try {
            val map = HashMap<String, Any>()
            session.getBody(map)
            val fileName = session.rawParams["fileName"] ?: return newFixedLengthResponse("error")
            val size = session.rawParams["size"] ?: return newFixedLengthResponse("error")
            val sliceSize = session.rawParams["sliceSize"] ?: return newFixedLengthResponse("error")
            val sliceNumber =
                session.rawParams["sliceNumber"] ?: return newFixedLengthResponse("error")
            val fileInfo = FileInfo(
                fileName as String,
                (size as String).toInt(),
                (sliceSize as String).toInt(),
                (sliceNumber as String).toInt()
            )
            fileUpload.startFileUpload(fileInfo)
            return newFixedLengthResponse("success")
        } catch (e: java.lang.Exception) {
            return newFixedLengthResponse("error")
        }
    }

    private fun fileUpload(session: IHTTPSession): Response {
        if (!fileUpload.isWritable) {
            Toast.makeText(MyApplication.context, "获取不到设备存储，无法传输文件！", Toast.LENGTH_SHORT).show()
            return newFixedLengthResponse("error")
        }
        try {
            val map = HashMap<String, Any>()
            session.getBody(map)
            val fileName = session.rawParams["fileName"] ?: return newFixedLengthResponse("error")
            val data = session.rawParams["data"] ?: return newFixedLengthResponse("error")
            val number = session.rawParams["sliceNumber"] ?: return newFixedLengthResponse("error")
            fileUpload.upload(
                fileName as String,
                data as ByteArray,
                (number as String).toInt(),
                this::onFinished
            )
            return newFixedLengthResponse("success")
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            return newFixedLengthResponse("error")
        }
    }

    fun onFinished(fileName: String) {
        // 移动文件

    }

}