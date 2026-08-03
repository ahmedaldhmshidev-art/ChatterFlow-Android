package com.example.chatapp.e_messageChatId.info.useCase

import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList

class GetOtherUserUseCase (
    private val repository: RepositoryUserList
) {
    suspend operator fun invoke(otherUser:String):User?{
        return repository.getUserById(otherUser)
    }
}