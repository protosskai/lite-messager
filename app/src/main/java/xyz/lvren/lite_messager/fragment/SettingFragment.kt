package xyz.lvren.lite_messager.fragment

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import xyz.lvren.lite_messager.MyApplication
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.databinding.FragmentSettingBinding
import xyz.lvren.lite_messager.http.FileUpload
import java.io.OutputStream

class SettingFragment : Fragment() {

    private val TAG = "xyz.lvren.lite_messager.fragment.SettingFragment"

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentSettingBinding>(
            inflater, R.layout.fragment_setting, container, false
        )
        val fileUpload = FileUpload(MyApplication.context)
        binding.testBtn.setOnClickListener {
            val outputStream = insertFileIntoMediaStore("test", "")
            outputStream?.write(10)
            outputStream?.close()
        }
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun insertFileIntoMediaStore(
        fileName: String,
        fileType: String
    ): OutputStream? {
        val resolver = MyApplication.context.contentResolver
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