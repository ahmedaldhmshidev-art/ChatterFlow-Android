package com.example.chatapp.a_authentication.stateAndEventAuth

import com.example.chatapp.a_authentication.errorHandler.AuthError

sealed interface AuthState {

    data object Idle : AuthState
    data object Loading : AuthState
    data class Error(val error: AuthError) : AuthState
}