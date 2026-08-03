package com.example.chatapp.c_listChatUser.accountMenu.domin

import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList

class GetAccountCurrentUseCase(
    private val repository:RepositoryUserList,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke():User? {
        val uid = sessionManager.currentUserId ?: return null
        return repository.getUserById(uid)
    }
}