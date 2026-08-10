package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.domin

import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList

class UpdateInfoAccountUseCase(
    private val sessionManager: SessionManager,
    private val repository: RepositoryUserList
) {
    suspend operator fun invoke(
        newName: String, newBio: String
    ): Result<Unit> {
        val uid = sessionManager.currentUserId ?: return Result.failure(
            IllegalArgumentException("User not logged in")
        )
        return repository.updateProfileAccount(
            uid = uid, newName = newName, newBio = newBio
        )
    }
}