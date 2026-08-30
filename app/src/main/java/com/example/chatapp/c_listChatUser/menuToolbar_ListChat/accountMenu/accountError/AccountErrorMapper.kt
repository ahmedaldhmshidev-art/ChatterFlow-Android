package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

object AccountErrorMapper {
    fun map(exception: Throwable): AccountError {
        return when (exception) {
            is FirebaseNetworkException ->
                AccountError.General.Network

            is FirebaseAuthInvalidCredentialsException ->
                AccountError.General.InvalidCurrentPassword

            else ->
                AccountError.General.Unknown
        }
    }
}