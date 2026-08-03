package com.example.chatapp.e_messageChatId.c_domain.unreadCount

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText

class IncreaseUnreadUseCase(private val unreadManager: UnreadManager) {
    suspend operator fun invoke(
        messageText: MessageText
    ){
        unreadManager.increaseUnread(
            message = messageText
        )
    }
}