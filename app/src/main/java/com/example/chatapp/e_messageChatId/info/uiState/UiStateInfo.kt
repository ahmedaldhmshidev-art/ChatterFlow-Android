package com.example.chatapp.e_messageChatId.info.uiState

import com.example.chatapp.a_authentication.modelAuth.User

data class UiStateInfo (
        val isLoading:Boolean = false,

        val user: User?=null,
        val error :String?=null
)