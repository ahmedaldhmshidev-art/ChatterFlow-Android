package com.example.chatapp.b_user_list.userError

sealed interface UserListError {
    data object Network : UserListError
    data object Unknown : UserListError
    data object NoSession : UserListError
}