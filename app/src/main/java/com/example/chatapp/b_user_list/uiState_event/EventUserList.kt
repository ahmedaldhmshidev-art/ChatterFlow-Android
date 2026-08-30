package com.example.chatapp.b_user_list.uiState_event

import com.example.chatapp.a_authentication.modelAuth.User

sealed interface EventUserList {
    data class OpenChat(
        val user: User
    ) : EventUserList
}