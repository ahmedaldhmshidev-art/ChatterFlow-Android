package com.example.chatapp.d_chat_Document.model_document

import com.example.chatapp.e_messageChatId.a_model_msg.ChatStatus

data class ChatDocument(
    val chatId:String="",
    val participants:List<String> = emptyList(),
    val lastMessage:LastMessage?=null ,
    val createdAt:Long=0,

    val typingUsers :Map<String,Boolean> = emptyMap(), // حالة الكتابة
    val unreadCountMap :Map<String ,Int>  = emptyMap(), // عدد الرسائل غير المقروءة

    val chatStatus :ChatStatus = ChatStatus.ACTIVE

)
