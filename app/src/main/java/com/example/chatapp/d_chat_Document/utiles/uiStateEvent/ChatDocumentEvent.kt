package com.example.chatapp.d_chat_Document.utiles.uiStateEvent

import com.example.chatapp.d_chat_Document.utiles.chatDocumentError.ChatDocumentError

sealed interface ChatDocumentEvent {
    data class Error(val error: ChatDocumentError) : ChatDocumentEvent
}