package com.example.chatapp.e_messageChatId.Error

import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import java.io.IOException

object MessageErrorMapper {
    fun map(exception: Exception): MessageError {

        return when (exception) {
            is FirebaseFirestoreException -> {
                when (exception.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                        MessageError.PermissionDenied

                    FirebaseFirestoreException.Code.UNAVAILABLE ->
                        MessageError.Offline

                    FirebaseFirestoreException.Code.NOT_FOUND ->
                        MessageError.NotFound

                    FirebaseFirestoreException.Code.FAILED_PRECONDITION ->
                        MessageError.FailedPrecondition

                    FirebaseFirestoreException.Code.ABORTED ->
                        MessageError.Aborted

                    FirebaseFirestoreException.Code.DEADLINE_EXCEEDED ->
                        MessageError.DeadlineExceeded

                    else ->
                        MessageError.Unknown
                }
            }

            is FirebaseAuthException ->
                MessageError.Authentication

            is IOException ->
                MessageError.Network

            else ->
                MessageError.Unknown
        }
    }
}