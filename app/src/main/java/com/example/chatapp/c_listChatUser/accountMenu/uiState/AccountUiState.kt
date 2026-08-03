package com.example.chatapp.c_listChatUser.accountMenu.uiState

import com.example.chatapp.a_authentication.modelAuth.User

data class AccountUiState(
    val isLoading:Boolean = false,
    val isSave:Boolean = false,
    val user:User?=null ,
    val error :String?=null
) {
}