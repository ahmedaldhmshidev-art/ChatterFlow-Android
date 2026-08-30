package com.example.chatapp.b_user_list.userError

import com.google.firebase.FirebaseNetworkException

object UserListErrorMapper {
    fun map(exception: Throwable): UserListError {
        return when (exception) {
            is FirebaseNetworkException ->
                UserListError.Network

            else ->
                UserListError.Unknown
        }
    }
}