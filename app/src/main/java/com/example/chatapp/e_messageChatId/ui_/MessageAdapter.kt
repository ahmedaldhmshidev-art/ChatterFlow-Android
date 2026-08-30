package com.example.chatapp.e_messageChatId.ui_

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.CardItemReceiverMessageLeftBinding
import com.example.chatapp.databinding.CardItemSendMessageRightBinding
import com.example.chatapp.databinding.ItemDateChatMessageBinding
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.ui_.menu.ActionMessage
import com.example.chatapp.e_messageChatId.ui_.menu.MessageOwner
import com.example.chatapp.mapper.MapperStatusMessageToMark
import com.example.chatapp.utils.ChatItem_dateAndMessageText
import com.example.chatapp.utils.styleTime

class MessageAdapter(
    private val currentUserId: String,
    private val onAction: (ActionMessage) -> Unit
) :
    ListAdapter<ChatItem_dateAndMessageText, RecyclerView.ViewHolder>(
        DIFF_CALLBACK
    ) {

    inner class RightSenderViewHolder(private val binding: CardItemSendMessageRightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // انا المرسل
        fun bindRightHolder(item: ChatItem_dateAndMessageText.MessageItem) {
            val message = item.message

            if (message.deleted) {
                binding.tvMessageSendId.setText(R.string.message_deleted_by_other)
            } else {
                binding.tvMessageSendId.text = message.messageText
            }
            binding.tvEditedSendId.isVisible = message.edited && !message.deleted
            binding.tvTimeSendItemSenderId.text = styleTime(message.timestamp)


            binding.tvStatusSendItemSender.isVisible = !message.deleted
            if (!message.deleted) {
                binding.tvStatusSendItemSender.text =
                    MapperStatusMessageToMark.mapToMark(message.statusMessage)
            }

            if (message.deleted) binding.tvMessageSendId.alpha = 0.5f
            else binding.tvMessageSendId.alpha = 1f

            setupMessageAction(message)
        }

        private fun setupMessageAction(message: MessageText) {
            if (message.deleted) {
                binding.root.isLongClickable = false
                binding.root.isClickable = false
                binding.root.setOnClickListener(null)
                binding.root.setOnLongClickListener(null)
            } else {
                binding.root.setOnLongClickListener {
                    onAction(
                        ActionMessage.ShowMenu(
                            message = message,
                            owner = MessageOwner.ME,
                            anchorView = binding.root
                        )
                    )
                    true
                }
            }
        }
    }

    // انا المستقبل
    inner class LeftReceiverViewHolder(
        private val binding: CardItemReceiverMessageLeftBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindLeftHolder(item: ChatItem_dateAndMessageText.MessageItem) {
            val message = item.message
            if (message.deleted) binding.tvMessageItemReceiver.setText(R.string.message_deleted_by_other)
            else binding.tvMessageItemReceiver.text = message.messageText

            binding.tvEditedItemReceiver.isVisible = message.edited && !message.deleted
            binding.tvTimeItemReceiver.text = styleTime(message.timestamp)

            if (message.deleted) binding.tvMessageItemReceiver.alpha = 0.5f
            else binding.tvMessageItemReceiver.alpha = 1f

            setupMessageAction(message)
        }

        private fun setupMessageAction(message: MessageText) {
            if (message.deleted) {
                binding.root.isClickable = false
                binding.root.isLongClickable = false
                binding.root.setOnLongClickListener(null)
                binding.root.setOnClickListener(null)
            } else {
                binding.root.setOnLongClickListener {
                    onAction(
                        ActionMessage.ShowMenu(
                            message = message,
                            owner = MessageOwner.OTHER,
                            anchorView = binding.root

                        )
                    )
                    true
                }

            }
        }
    }

    // لتتاريخ
    class DateItemViewHolder(private val binding: ItemDateChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun buildDate(date: String) {
            binding.tvDateItemMessage.text = date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_RIGHT_SENDER -> {
                val rightInflate = CardItemSendMessageRightBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return RightSenderViewHolder(rightInflate)
            }

            TYPE_LEFT_RECEIVER -> {
                val leftInflate = CardItemReceiverMessageLeftBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return LeftReceiverViewHolder(leftInflate)
            }

            TYPE_DATE -> {
                val binding = ItemDateChatMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return DateItemViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (val item = getItem(position)) {
            is ChatItem_dateAndMessageText.DateItem -> {
                (holder as DateItemViewHolder).buildDate(item.date)
            }

            is ChatItem_dateAndMessageText.MessageItem -> {
                when (holder) {
                    is LeftReceiverViewHolder -> {
                        holder.bindLeftHolder(item)
                    }

                    is RightSenderViewHolder -> {
                        holder.bindRightHolder(item)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {

        return when (val item = getItem(position)) {
            is ChatItem_dateAndMessageText.MessageItem -> {

                if (item.message.senderId == currentUserId) {
                    TYPE_RIGHT_SENDER
                } else {
                    TYPE_LEFT_RECEIVER
                }
            }

            is ChatItem_dateAndMessageText.DateItem -> {
                TYPE_DATE
            }
        }
    }

    companion object {
        // viewTypes
        private const val TYPE_DATE = 3
        private const val TYPE_RIGHT_SENDER = 1
        private const val TYPE_LEFT_RECEIVER = 2

        // diffUtil list adapter
        // هذا يعمل مقارنة لرسائل بحيث اذا الرسالة موجودة بس محتوها تغير يعمل تحديث فقط
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChatItem_dateAndMessageText>() {
            // دالة ترجع true اذا كان الرسالة الجديدة هي نفسها القديمة او موجودة مسبقا
            override fun areItemsTheSame(
                oldItem: ChatItem_dateAndMessageText,
                newItem: ChatItem_dateAndMessageText
            ): Boolean {
                return when {
                    oldItem is ChatItem_dateAndMessageText.MessageItem
                            && newItem is ChatItem_dateAndMessageText.MessageItem -> {
                        oldItem.message.messageId == newItem.message.messageId
                    }

                    oldItem is ChatItem_dateAndMessageText.DateItem
                            && newItem is ChatItem_dateAndMessageText.DateItem -> {
                        oldItem.date == newItem.date
                    }

                    else -> false
                }

            }

            // دالة ترجع true اذا محتوي الرسالة موجود
            override fun areContentsTheSame(
                oldItem: ChatItem_dateAndMessageText,
                newItem: ChatItem_dateAndMessageText
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

}