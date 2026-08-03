package com.example.chatapp.a_authentication.stateAndEventAuth

sealed interface AuthEvent {
    data object NavigationToHomeListChat : AuthEvent
    data object NavigationToLogin: AuthEvent
}