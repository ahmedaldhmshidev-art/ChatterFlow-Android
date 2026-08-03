package com.example.chatapp.a_authentication.useCase

import com.example.chatapp.a_authentication.repositoryAuth.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        name:String , email:String , password:String
    ){
        repository.registerUser(
            name, email, password
        )
    }
}