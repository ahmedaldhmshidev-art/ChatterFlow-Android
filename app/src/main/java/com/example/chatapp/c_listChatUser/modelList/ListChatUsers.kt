package com.example.chatapp.c_listChatUser.modelList

import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.a_model_msg.TypeMessage

data class ListChatUsers (
    val chatId         :String = "",
    val userId         :String = "",

    val userName       :String = "",
    val userImg        :String = "",

    val lastMessage    :String ?="",
    val timeLastMessage:Long   ?= 0L ,

    val lastMessageSenderId :String ?= "",

    val statusMessage  : StatusMessage = StatusMessage.SENT,

    val unreadCountMap : Int = 0,

    val typeMessage: TypeMessage = TypeMessage.TEXT,
    val typingUsers :Boolean = false ,// حالة الكتابة

)