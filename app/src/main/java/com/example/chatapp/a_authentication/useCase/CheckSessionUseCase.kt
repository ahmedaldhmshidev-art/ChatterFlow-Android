package com.example.chatapp.a_authentication.useCase

import com.example.chatapp.a_authentication.SessionManager

class CheckSessionUseCase(
    private val sessionManager: SessionManager
) {
    operator fun invoke():Boolean{
        return sessionManager.isLoggedIn
    }
}