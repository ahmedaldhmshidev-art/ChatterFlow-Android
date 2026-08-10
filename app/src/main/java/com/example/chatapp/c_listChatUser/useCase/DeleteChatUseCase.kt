package com.example.chatapp.c_listChatUser.useCase

import com.example.chatapp.c_listChatUser.repository_List.ListChatRepository

class DeleteChatUseCase(
    private val repository: ListChatRepository
) {
    suspend operator fun invoke(chatId:String):Result<Unit>{
       return repository.deleteChat(chatId = chatId)
    }
}