package com.example.chatapp.d_chat_Document.model_document

import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.a_model_msg.TypeMessage

data class LastMessage(
     val senderIdLastMessage :String="",
     val lastMessageId :String ="",
     val lastMessageText :String ="",

     val timeLastMessage :Long =0L,
     val statusLastMessage :StatusMessage = StatusMessage.SENT,
//     String = StatusMessage.PENDING.name,
     val typeLastMessage :TypeMessage = TypeMessage.TEXT,

 )