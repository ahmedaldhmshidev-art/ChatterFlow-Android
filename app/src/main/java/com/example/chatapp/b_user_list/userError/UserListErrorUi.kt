package com.example.chatapp.b_user_list.userError

import com.example.chatapp.R

object UserListErrorUi {
    fun map(error: UserListError): Int {
        return when (error) {
            UserListError.NoSession ->
                R.string.error_no_session

            UserListError.Network ->
                R.string.error_network_error_AuthError

            UserListError.Unknown ->
                R.string.error_unknown_error_AuthError
        }
    }
}