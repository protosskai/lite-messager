package xyz.lvren.lite_messager.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.lvren.lite_messager.MyApplication
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.adapter.MessageItemDecoration
import xyz.lvren.lite_messager.adapter.MessageRecyclerViewAdapter
import xyz.lvren.lite_messager.adapter.RecyclerItemClickListener
import xyz.lvren.lite_messager.databinding.FragmentMainBinding
import xyz.lvren.lite_messager.entity.DataSource
import xyz.lvren.lite_messager.util.NetWorkUtil
import java.util.*


class MainFragment : Fragment() {
    private val TAG = "xyz.lvren.lite_messager.fragment.MainFragment"
    private lateinit var binding: FragmentMainBinding
    private val networkUtil = NetWorkUtil(MyApplication.context)
    private val messageList = DataSource.messages
    private val adapter = MessageRecyclerViewAdapter(messageList)
    private val timer = Timer()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("onCreateView")
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false
        )
        initView()

        return binding.root
    }

    private fun initView() {
        with(binding) {
            messageRecycleView.setHasFixedSize(true)
            messageRecycleView.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            messageRecycleView.adapter = adapter
            messageRecycleView.addOnItemTouchListener(
                RecyclerItemClickListener(context, messageRecycleView,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View?, position: Int) {
                            val clipboard =
                                activity!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip: ClipData =
                                ClipData.newPlainText("message", messageList[position].message)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(MyApplication.context, "复制成功！", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onLongItemClick(view: View?, position: Int) {
                            Log.d(TAG, "${messageList[position].message} 被长按了")
                        }

                    })
            )
            val largePadding = resources.getDimensionPixelSize(R.dimen.shr_product_grid_spacing)
            val smallPadding =
                resources.getDimensionPixelSize(R.dimen.shr_product_grid_spacing_small)
            messageRecycleView.addItemDecoration(
                MessageItemDecoration(
                    largePadding,
                    smallPadding
                )
            )
            var networkStateStr = if (networkUtil.getWifiConnected()) {
                val ip = networkUtil.getLocalV4Address()?.toString()?.trim('/') ?: ""
                val port = "8888"
                "请连接到：http://${ip}:${port} 来使用"
            } else {
                "请检查你的WiFi连接！"
            }
            networkState.text = networkStateStr
        }
    }

    override fun onStart() {
        super.onStart()
        val timerTask = object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    if (DataSource.messages.size != 0) {
                        binding.networkState.visibility = View.GONE
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
        timer.schedule(timerTask, 0, 500)
    }

    override fun onPause() {
        super.onPause()
        timer.purge()
    }

}