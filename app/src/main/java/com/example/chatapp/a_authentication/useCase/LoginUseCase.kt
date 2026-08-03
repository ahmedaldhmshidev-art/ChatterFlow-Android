package com.example.chatapp.a_authentication.useCase

import com.example.chatapp.a_authentication.repositoryAuth.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email:String , password:String
    ){
        repository.signUser(
            email=email, password=password
        )
    }
}