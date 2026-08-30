package com.example.chatapp.c_listChatUser.utiles.listChatError

import com.google.firebase.FirebaseNetworkException

object ListChatErrorMapper {
    fun map(throwable: Throwable): ListChatError {
        return when (throwable) {
            is FirebaseNetworkException ->
                ListChatError.Network

            else ->
                ListChatError.Unknown
        }
    }
}