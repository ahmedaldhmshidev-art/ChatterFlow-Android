package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState

import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError.AccountError

data class AccountUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: AccountError? = null
)