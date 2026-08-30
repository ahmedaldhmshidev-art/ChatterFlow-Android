package com.example.chatapp.d_chat_Document.utiles.chatDocumentError

import com.example.chatapp.R

object ChatDocumentErrorUi {
    fun messageRef(error: ChatDocumentError): Int {
        return when (error) {
            ChatDocumentError.NetworkError ->
                R.string.error_network_error_AuthError

            ChatDocumentError.PermissionDenied ->
                R.string.error_permission_denied

            ChatDocumentError.NoSession ->
                R.string.error_no_session

            ChatDocumentError.Unknown ->
                R.string.error_unknown_error_AuthError
        }
    }
}