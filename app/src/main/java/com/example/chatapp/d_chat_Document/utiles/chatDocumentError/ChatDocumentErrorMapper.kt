package com.example.chatapp.d_chat_Document.utiles.chatDocumentError

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException

object ChatDocumentErrorMapper {
    fun map(exception: Throwable): ChatDocumentError {
        return when (exception) {
            is FirebaseNetworkException ->
                ChatDocumentError.NetworkError

            is FirebaseFirestoreException -> {
                when (exception.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                        ChatDocumentError.PermissionDenied

                    else ->
                        ChatDocumentError.Unknown
                }
            }

            else -> ChatDocumentError.Unknown
        }
    }
}