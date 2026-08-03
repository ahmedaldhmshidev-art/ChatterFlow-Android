package com.example.chatapp.e_messageChatId.stateAndEvent_msg

import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText

data class ChatScreenState(

   val isLoading :Boolean = false ,
   val toolbarUser: User?= null ,
   val messages:List<MessageText> = emptyList(),

)
