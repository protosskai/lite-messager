package xyz.lvren.lite_messager.http

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import xyz.lvren.lite_messager.MyApplication
import xyz.lvren.lite_messager.entity.FileInfo
import java.io.*
import kotlin.collections.LinkedHashMap

/**
 * 文件上传类，主要用来处理前端分片上传文件
 */
class FileUpload(val context: Context) {

    private val taskMap: MutableMap<String, FileInfo> = LinkedHashMap()
    var isWritable: Boolean
    var baseDir: File

    init {
        isWritable = isExternalStorageWritable()
        baseDir = getPrimaryExternalStorage()
    }

    // 开始上传文件请求，在此处做一些准备
    fun startFileUpload(fileInfo: FileInfo) {
        if (!isWritable)
            return
        taskMap[fileInfo.fileName] = fileInfo
    }

    // 处理分片文件的上传
    fun upload(fileName: String, data: ByteArray, number: Int, onFinished: (String) -> Unit) {
        if (!isWritable)
            return
        val fileInfo = taskMap[fileName] ?: return
        val tmpFileName = "${fileName}-${number}"
        // 存到缓存目录中
        val outFile = File(context.cacheDir, tmpFileName)
        val outputStream = FileOutputStream(outFile)
        outputStream.write(data)
        outputStream.close()
        fileInfo.finishedSliceMap[number] = true
        if (checkFinished(fileName)) {
            mergeSlice(fileName)
            cleanTmpFile(fileName)
            onFinished(fileName)
        }
    }

    // 检查文件所有分片是否都上传完成
    private fun checkFinished(fileName: String): Boolean {
        val fileInfo = taskMap[fileName] ?: return false
        return fileInfo.checkFinished()
    }

    // 合并文件，合并完成后会在目录下生成与文件名同名的文件
    private fun mergeSlice(fileName: String): Boolean {
        val fileInfo = taskMap[fileName] ?: return false
        try {
            val outFile: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                insertFileIntoDownload(fileName, "")
            } else {
                BufferedOutputStream(FileOutputStream(File(baseDir, fileName)))
            }
            for (i in 0 until fileInfo.sliceNumber) {
                val tmpInput =
                    BufferedInputStream(FileInputStream(File(context.cacheDir, "${fileName}-${i}")))
                val byteArray = ByteArray(1024)
                while (tmpInput.read(byteArray) > 0) {
                    outFile?.write(byteArray)
                }
                tmpInput.close()
            }
            outFile?.close()
            return true
        } catch (e: IOException) {
            return false
        }
    }

    // 清理临时文件
    private fun cleanTmpFile(fileName: String) {
        val fileInfo = taskMap[fileName] ?: return
        for (i in 0 until fileInfo.sliceNumber) {
            val tmpFile = File(context.cacheDir, "${fileInfo.fileName}-${i}")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
        }
    }

    // 验证是否外部存储可读写
    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun getPrimaryExternalStorage(): File {
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(context, null)
        return if (externalStorageVolumes.isNotEmpty()) {
            externalStorageVolumes[0]
        } else {
            // 这个分支不会到达
            File(context.cacheDir, "")
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertFileIntoDownload(
        fileName: String,
        fileType: String
    ): OutputStream? {
        val resolver = context.contentResolver
        val values = ContentValues()
        //设置文件名
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
        //设置文件类型
        if (fileType.isNotEmpty()) {
            values.put(MediaStore.Downloads.MIME_TYPE, fileType)
        }
        val external = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        //insertUri表示文件保存的uri路径
        val uri = resolver.insert(external, values)
        if (uri != null) {
            return resolver.openOutputStream(uri)
        }
        return null
    }
}