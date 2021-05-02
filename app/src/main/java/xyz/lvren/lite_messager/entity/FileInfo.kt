package xyz.lvren.lite_messager.entity

import com.google.gson.Gson
import java.lang.StringBuilder

class FileInfo(
    val fileName: String,
    val size: Int,
    val sliceSize: Int,
    val sliceNumber: Int
) {
    var finishedSliceMap: ArrayList<Boolean>

    init {
        finishedSliceMap = ArrayList(sliceNumber)
        for (i in 0 until sliceNumber) {
            finishedSliceMap.add(false)
        }
    }

    fun checkFinished(): Boolean {
        for (i in 0 until finishedSliceMap.size) {
            if (!finishedSliceMap[i])
                return false
        }
        return true
    }

    // 获取没有上传完毕的分片
    fun getUnfinishedSlice(): String {
        val gson = Gson()
        val result = ArrayList<Int>()
        for (i in 0 until finishedSliceMap.size) {
            if (!finishedSliceMap[i])
                result.add(i)
        }
        return gson.toJson(result)
    }
}
