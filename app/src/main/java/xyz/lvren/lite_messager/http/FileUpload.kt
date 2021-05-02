package xyz.lvren.lite_messager.http

import android.content.Context
import org.nanohttpd.protocols.http.IHTTPSession
import xyz.lvren.lite_messager.entity.FileInfo
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * 文件上传类，主要用来处理前端分片上传文件
 */
class FileUpload(val context: Context) {

    private val taskMap: MutableMap<String, FileInfo> = LinkedHashMap()

    private fun createFile(fileName: String): Boolean {
        return try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) {
                file.createNewFile();
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 开始上传文件请求，在此处做一些准备
    fun startFileUpload(fileInfo: FileInfo) {
        taskMap[fileInfo.fileName] = fileInfo
    }

    // 处理分片文件的上传
    fun upload(fileName: String, data: ByteArray, number: Int) {
        val fileInfo = taskMap[fileName] ?: return
        val tmpFileName = "${fileName}-${number}"
        val outputStream = context.openFileOutput(tmpFileName, Context.MODE_PRIVATE)
        outputStream.write(data)
        outputStream.close()
        fileInfo.finishedSliceMap[number] = true
        if (checkFinished(fileName)) {
            mergeSlice(fileName)
        }
    }

    // 检查文件所有分片是否都上传完成
    fun checkFinished(fileName: String): Boolean {
        val fileInfo = taskMap[fileName] ?: return false
        return fileInfo.checkFinished()
    }

    // 合并文件，合并完成后会在目录下生成与文件名同名的文件
    fun mergeSlice(fileName: String): Boolean {
        val fileInfo = taskMap[fileName] ?: return false
        try {
            val outFile =
                BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE))
            for (i in 0 until fileInfo.sliceNumber) {
                val tmpInput = BufferedInputStream(context.openFileInput("${fileName}-${i}"))
                val byteArray = ByteArray(1024)
                while (tmpInput.read(byteArray) > 0) {
                    outFile.write(byteArray)
                }
                tmpInput.close()
            }
            outFile.close()
            return true
        } catch (e: IOException) {
            return false
        }
    }

}