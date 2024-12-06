package com.koi.projectlib.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.koi.projectlib.R
import com.koi.projectlib.model.Message

class ChatAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_USER) {
            val view = inflater.inflate(R.layout.item_message_user, parent, false)
            UserMessageViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_message_ai, parent, false)
            AIMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserMessageViewHolder) {
            holder.bind(message.content)
        } else if (holder is AIMessageViewHolder) {
            holder.bind(message.content)
        }
    }

    override fun getItemCount() = messages.size

    class UserMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(content: String) {
            (itemView.findViewById<TextView>(R.id.tvUserMsg) as TextView).text = content
        }
    }

    class AIMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(content: String) {
            (itemView as TextView).text = content
        }
    }
}