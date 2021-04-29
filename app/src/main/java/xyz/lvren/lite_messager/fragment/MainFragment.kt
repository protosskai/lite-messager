package xyz.lvren.lite_messager.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.adapter.MessageItemDecoration
import xyz.lvren.lite_messager.adapter.MessageRecyclerViewAdapter
import xyz.lvren.lite_messager.adapter.RecyclerItemClickListener
import xyz.lvren.lite_messager.databinding.FragmentMainBinding
import xyz.lvren.lite_messager.entity.Message
import java.util.*


class MainFragment : Fragment() {
    private val TAG = "xyz.lvren.lite_messager.fragment.MainFragment"
    private lateinit var binding: FragmentMainBinding
    private val messageList = LinkedList<Message>()
    private val adapter = MessageRecyclerViewAdapter(messageList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentMainBinding>(
            inflater, R.layout.fragment_main, container, false
        )
        messageList.add(Message("hello!"))
        messageList.add(Message("good!!"))
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
                            Log.d(TAG, "${messageList[position].message} 被点击了")
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
        }
    }
}