package com.ami.chamba_pofabo.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ami.chamba_pofabo.databinding.ItemMessageReceivedBinding
import com.ami.chamba_pofabo.databinding.ItemMessageSentBinding
import com.ami.chamba_pofabo.model.Message

class ChatAdapter(
    private var messages: ArrayList<Message> = arrayListOf(),
    private val myId: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_SENT = 0
        private const val VIEW_RECEIVED = 1
    }

    fun setData(newMessages: ArrayList<Message>) {
        messages = newMessages
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender.id == myId ||
            (myId == 0 && messages[position].sender.id == 44)) {
            VIEW_SENT
        } else {
            VIEW_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_SENT) {
            val binding = ItemMessageSentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            SentViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ReceivedViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is SentViewHolder) holder.bind(msg)
        else if (holder is ReceivedViewHolder) holder.bind(msg)
    }

    override fun getItemCount(): Int = messages.size

    class SentViewHolder(private val binding: ItemMessageSentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.tvMensaje.text = msg.message
        }
    }

    class ReceivedViewHolder(private val binding: ItemMessageReceivedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: Message) {
            binding.tvMensaje.text = msg.message
        }
    }
}
