package com.example.chatapp.b_user_list.uiState_event

import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.b_user_list.userError.UserListError

sealed class StateUserList {
    data object Idle : StateUserList()
    data object Loading : StateUserList()

    data class Success(val users: List<User>) : StateUserList()
    data object Empty : StateUserList()

    data class SearchEmpty(val query: String) : StateUserList()

    data class Error(val error: UserListError) : StateUserList()
}