package com.example.chatapp.c_listChatUser.utiles.listChatError

import com.example.chatapp.R

object ListChatErrorUi {
    fun map(error: ListChatError): Int {
        return when (error) {
            ListChatError.Network ->
                R.string.error_network_error_AuthError

            ListChatError.NoSession ->
                R.string.error_no_session

            ListChatError.Unknown ->
                R.string.error_unknown_error_AuthError
        }
    }
}