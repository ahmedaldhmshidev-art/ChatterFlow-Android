package com.example.chatapp.e_messageChatId.c_domain.useCase

import android.util.Log
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.b_data.repository_msg.MessageRepository
import kotlinx.coroutines.flow.Flow

class GetAllMessageUseCase(
    private val msgRepository: MessageRepository
) {
    operator fun invoke (chatId:String):Flow<List<MessageText>>{
        val messages = msgRepository.getMessage(chatId)
        Log.d("getAllMessageRepository","getAllMessageUseCaseIsDelete:${messages}")

        return messages
    }
}