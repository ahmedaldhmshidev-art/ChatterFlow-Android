package com.example.chatapp.d_chat_Document.utiles.uesCase

import com.example.chatapp.d_chat_Document.repository_document.ChatDocumentRepository

class InitializeChatUseCase(
    private val repository: ChatDocumentRepository
) {
    suspend operator fun invoke(
        chatId: String, participants: List<String>
    ) {
        repository.createChatDocumentIfNotExist(
            chatId, participants
        )
    }

}