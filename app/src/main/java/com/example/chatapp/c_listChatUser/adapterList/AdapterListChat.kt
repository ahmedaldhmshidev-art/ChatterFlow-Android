package com.example.chatapp.c_listChatUser.adapterList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.databinding.CardItemListChatBinding
import com.example.chatapp.c_listChatUser.modelList.ListChatUsers
import com.example.chatapp.mapper.MapperStatusMessageToMark
import com.example.chatapp.utils.styleTime

class AdapterListChat( private val currentId:String ,private val navigationToMessage:(ListChatUsers)->Unit ):
     ListAdapter<ListChatUsers , AdapterListChat.HolderListChat>(ListChatDiffUtil())
{
    class HolderListChat(private val binding:CardItemListChatBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: ListChatUsers , currentId: String) {
            binding.tvNameItemListChatId.text = chat.userName
            binding.tvMessageItemListChatId.text = chat.lastMessage
            binding.tvTimeItemListChatId.text= styleTime(chat.timeLastMessage?:0)


            if (chat.unreadCountMap > 0) {
                binding.tvUnreadItemListChatId.text = chat.unreadCountMap.toString()
                binding.tvUnreadItemListChatId.visibility = View.VISIBLE
            }else{
                binding.tvUnreadItemListChatId.visibility=View.GONE
            }


            val showStatus = chat.lastMessageSenderId == currentId

            if (showStatus) {
                binding.tvStatusItemListChatId.text =
                    MapperStatusMessageToMark.mapToMark(chat.statusMessage)
                binding.tvStatusItemListChatId.visibility = View.VISIBLE
            }
            else{
                binding.tvStatusItemListChatId.visibility = View.GONE
            }
        }

    }

    class ListChatDiffUtil: DiffUtil.ItemCallback<ListChatUsers>() {
        override fun areItemsTheSame(oldItem: ListChatUsers, newItem: ListChatUsers): Boolean {
            return oldItem.chatId == newItem.chatId
        }
        override fun areContentsTheSame(oldItem: ListChatUsers, newItem: ListChatUsers): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderListChat {
         val binding = CardItemListChatBinding.
         inflate(LayoutInflater.from(parent.context),parent,false)
         return HolderListChat(binding)
     }


     override fun onBindViewHolder(holder: HolderListChat, position: Int) {
         val chat = getItem(position)
         holder.bind(chat , currentId)

         holder.itemView.setOnClickListener {
             navigationToMessage(chat)

         }
     }
 }