package xyz.lvren.lite_messager.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import xyz.lvren.lite_messager.MyApplication
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.databinding.FragmentSettingBinding
import xyz.lvren.lite_messager.http.FileUpload
import xyz.lvren.lite_messager.util.NetWorkUtil
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
        binding.testBtn.setOnClickListener {

        }
        return binding.root
    }


}