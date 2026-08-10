package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin

import com.example.chatapp.a_authentication.repositoryAuth.AuthRepository

class ChangePasswordUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return authRepository.changePassword(
            currentPassword = currentPassword,
            newPassword = newPassword
        )
    }
}