package com.example.chatapp.d_chat_Document.utiles.chatDocumentError

sealed interface ChatDocumentError {

    data object NetworkError : ChatDocumentError
    data object PermissionDenied : ChatDocumentError
    data object NoSession : ChatDocumentError
    data object Unknown : ChatDocumentError
}