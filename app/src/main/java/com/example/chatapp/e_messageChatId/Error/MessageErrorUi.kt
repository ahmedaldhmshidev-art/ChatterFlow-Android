package com.example.chatapp.e_messageChatId.Error

import com.example.chatapp.R

object MessageErrorUi {
    fun getErrorMessage(error: MessageError): Int {
        return when (error) {
            MessageError.PermissionDenied ->
                R.string.error_permission_denied

            MessageError.Offline ->
                R.string.error_offline

            MessageError.NotFound ->
                R.string.error_not_found

            MessageError.FailedPrecondition ->
                R.string.error_failed_precondition

            MessageError.Aborted -> R.string.error_aborted

            MessageError.DeadlineExceeded ->
                R.string.error_deadline_exceeded

            MessageError.Authentication ->
                R.string.error_authentication

            MessageError.Network ->
                R.string.error_network

            MessageError.Unknown ->
                R.string.error_unknown
        }
    }
}