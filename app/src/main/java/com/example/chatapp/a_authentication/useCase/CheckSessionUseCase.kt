package com.example.chatapp.a_authentication.useCase

import com.example.chatapp.a_authentication.SessionManager

//  يستخدم في main من اجل الانتقال الي الصفحة المناسبة
class CheckSessionUseCase(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Boolean {
        return sessionManager.isLoggedIn
    }
}