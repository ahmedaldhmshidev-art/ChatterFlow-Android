package com.example.chatapp.a_authentication.stateAndEventAuth

sealed interface AuthError {
//    error firebase
    data object EmailAlreadyExists : AuthError
    data object WeakPassword : AuthError
    data object InvalidCredentials : AuthError
    data object NetworkError : AuthError

//    validation error view
    data object EmptyName: AuthError
    data object EmptyEmail: AuthError
    data object EmptyPassword: AuthError
    data object InvalidEmail: AuthError

    data object Unknown : AuthError


}
