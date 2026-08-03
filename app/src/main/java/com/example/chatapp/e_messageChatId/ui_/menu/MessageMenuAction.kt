package com.example.chatapp.e_messageChatId.ui_.menu

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText

sealed interface MessageMenuAction {
    data class Edit(val message:MessageText) :MessageMenuAction
    data class Copy(val text:String) :MessageMenuAction
    data class Delete(val message:MessageText):MessageMenuAction
}