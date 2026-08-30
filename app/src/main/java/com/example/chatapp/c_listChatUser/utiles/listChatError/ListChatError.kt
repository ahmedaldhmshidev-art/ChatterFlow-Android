package com.example.chatapp.c_listChatUser.utiles.listChatError

sealed interface ListChatError {
    data object Network : ListChatError

    //    data object ChatNotFound:ListChatError
    data object NoSession : ListChatError
    data object Unknown : ListChatError
}