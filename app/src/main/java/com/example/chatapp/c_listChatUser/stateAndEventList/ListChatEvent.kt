package com.example.chatapp.c_listChatUser.stateAndEventList

sealed interface ListChatEvent {
    data class NavigationToMessageListChat(
        val userUid:String
    ): ListChatEvent
}