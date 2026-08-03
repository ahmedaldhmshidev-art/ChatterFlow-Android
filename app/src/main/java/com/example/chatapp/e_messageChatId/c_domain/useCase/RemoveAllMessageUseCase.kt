package com.example.chatapp.e_messageChatId.c_domain.useCase

import com.example.chatapp.e_messageChatId.b_data.repository_msg.MessageRepository
import com.example.chatapp.e_messageChatId.b_data.repository_msg.UpdateChatDocumentLastMsg
import com.example.chatapp.e_messageChatId.c_domain.unreadCount.ResetUnreadUseCase

class RemoveAllMessageUseCase(
    private val repository: MessageRepository ,
    private val chatDocumentLastMsg: UpdateChatDocumentLastMsg,
    private val resetUnreadUseCase: ResetUnreadUseCase
) {
    suspend operator fun invoke(chatId:String , currentUserId:String):Result<Unit>{
        return runCatching {
            repository.removeAllMessage(chatId=chatId)

            chatDocumentLastMsg.removeLastMessage(chatId = chatId)

            resetUnreadUseCase(chatId = chatId , currentUserId = currentUserId)
        }
    }
}