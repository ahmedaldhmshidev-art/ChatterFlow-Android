package com.example.chatapp.a_authentication.stateAndEventAuth

sealed interface AuthState {

    data object Idle : AuthState
    data object Loading : AuthState
    data class Error(
        val error: AuthError
    ): AuthState
}