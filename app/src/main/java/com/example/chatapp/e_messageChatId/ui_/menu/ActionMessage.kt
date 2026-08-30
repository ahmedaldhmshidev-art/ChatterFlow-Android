package com.example.chatapp.e_messageChatId.ui_.menu

import android.view.View
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText

sealed interface ActionMessage {
    data class ShowMenu(
        val message: MessageText,
        val owner: MessageOwner,
        val anchorView: View
    ) : ActionMessage
}

enum class MessageOwner {
    ME, OTHER
}