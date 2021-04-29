package xyz.lvren.lite_messager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentSettingBinding>(
            inflater, R.layout.fragment_setting, container, false
        )
        return binding.root
    }
}