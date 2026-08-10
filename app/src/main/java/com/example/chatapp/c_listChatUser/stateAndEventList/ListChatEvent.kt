package com.example.chatapp.c_listChatUser.stateAndEventList

import com.example.chatapp.a_authentication.stateAndEventAuth.AuthEvent

sealed interface ListChatEvent {
    data class NavigationToMessageListChat(
        val userUid: String
    ) : ListChatEvent

    data class Error(val message: String) : ListChatEvent

    data object SuccessDeleteChat : ListChatEvent

    data object NavigationToLogin : ListChatEvent
}