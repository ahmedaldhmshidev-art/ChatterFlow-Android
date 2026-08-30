package com.example.chatapp.a_authentication.errorHandler

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.lang.Exception

object AuthErrorMapper {
    fun mapError(exception: Exception): AuthError {
        return when (exception) {
            is FirebaseAuthUserCollisionException ->
                AuthError.EmailAlreadyExists

            is FirebaseAuthWeakPasswordException ->
                AuthError.WeakPassword

            is FirebaseAuthInvalidCredentialsException ->
                AuthError.InvalidCredentials

            is FirebaseNetworkException ->
                AuthError.NetworkError

            else ->
                AuthError.Unknown
        }
    }
}