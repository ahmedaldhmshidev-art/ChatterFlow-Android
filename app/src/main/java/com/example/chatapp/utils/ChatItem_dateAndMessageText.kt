package com.example.chatapp.utils

import android.util.Log
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText

sealed class ChatItem_dateAndMessageText {
    data class MessageItem (
        val message: MessageText
    ):ChatItem_dateAndMessageText()

    data class DateItem(
        val date:String
    ):ChatItem_dateAndMessageText()
}
// حولي من messageText الي يقبلها adapter بدون تاريخ الي messageItem مدمج التاريخ مع الرسالة من اجل عرضها في recyclerView
fun buildChatItem(message:List<MessageText>):List<ChatItem_dateAndMessageText>{
    val chatItem = mutableListOf<ChatItem_dateAndMessageText>()
    var lastDateMsg = ""
    for (msg in message){
        val currentDateMsg = getMessageDay(msg.timestamp) // ترجع today or yesterday or 2026

        if (currentDateMsg != lastDateMsg){
            chatItem.add(ChatItem_dateAndMessageText.DateItem(currentDateMsg))
            lastDateMsg = currentDateMsg
        }
        chatItem.add(ChatItem_dateAndMessageText.MessageItem(msg))
    }
    chatItem.forEach {
        if (it is ChatItem_dateAndMessageText.MessageItem){
            Log.d("ChatItem_dateAndMessageText.MessageItem","ChatItem_dateAndMessageText.MessageItemIsDelete:${it.message.deleted}, isEdit:${it.message.edited}")
        }
    }
    return chatItem
}