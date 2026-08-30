package com.example.chatapp.d_chat_Document.utiles.uiStateEvent

import com.example.chatapp.d_chat_Document.utiles.chatDocumentError.ChatDocumentError

sealed interface ChatDocumentState {
    data object Idle : ChatDocumentState
    data object Loading : ChatDocumentState
    data object Success : ChatDocumentState
    data class Error(val error: ChatDocumentError) : ChatDocumentState
}