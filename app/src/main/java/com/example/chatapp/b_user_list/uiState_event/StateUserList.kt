package com.example.chatapp.b_user_list.uiState_event

import com.example.chatapp.a_authentication.modelAuth.User

sealed class StateUserList {
   data object Idle :StateUserList()
    data object Loading:StateUserList()

    data class Success(val users:List<User>):StateUserList()
    data object Empty:StateUserList()

    data class Error (val message:String ):StateUserList()
}