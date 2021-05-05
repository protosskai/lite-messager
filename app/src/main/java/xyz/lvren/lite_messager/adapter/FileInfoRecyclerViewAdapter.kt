package xyz.lvren.lite_messager.adapter

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import xyz.lvren.lite_messager.R
import xyz.lvren.lite_messager.entity.FileMessage
import xyz.lvren.lite_messager.entity.Message
import xyz.lvren.lite_messager.entity.TextMessage

class MessageRecyclerViewAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutView =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_message, parent, false)
        return MessageViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageItem = messageList[position]
        holder.messageText.text = messageItem.message
        if (messageItem is TextMessage) {
            holder.messageText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_text_24,
                0,
                0,
                0
            )
        } else if (messageItem is FileMessage) {
            holder.messageText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_file_24,
                0,
                0,
                0
            )
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

}


class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var messageText: MaterialTextView = itemView.findViewById(R.id.messageText)
}

class MessageItemDecoration(private val largePadding: Int, private val smallPadding: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = smallPadding
        outRect.right = smallPadding
        outRect.top = largePadding
        outRect.bottom = largePadding
    }
}
