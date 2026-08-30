package com.example.chatapp.c_listChatUser.utiles.stateAndEventList

import com.example.chatapp.c_listChatUser.utiles.listChatError.ListChatError

sealed interface ListChatEvent {
    data class OpenChatMessage(val userUid: String) : ListChatEvent
    data class ShowError(val error: ListChatError) : ListChatEvent
    data object DeleteChatSuccess : ListChatEvent
    data object NavigationToLogin : ListChatEvent
}