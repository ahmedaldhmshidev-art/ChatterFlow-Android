package com.example.chatapp.a_authentication.useCase

import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.e_messageChatId.c_domain.delivered.DeliveredObserver

class LogoutUseCase(
    private val sessionManager: SessionManager,
    private val deliveredObserver: DeliveredObserver
) {
    operator fun invoke(){
        try {
            deliveredObserver.stop()
        }finally {
            sessionManager.logout()
        }
    }
}