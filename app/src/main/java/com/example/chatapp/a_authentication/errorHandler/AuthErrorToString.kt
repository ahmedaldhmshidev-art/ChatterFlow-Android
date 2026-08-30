package com.example.chatapp.a_authentication.errorHandler

import com.example.chatapp.R

// يعمل الرسالة المناسبة حسب الخطا

object AuthErrorToString {

    fun AuthError.toMessageRes(): Int {
        return when (this) {
            // field errors
            AuthError.EmptyName ->
                R.string.error_empty_name_validError
            // email
            AuthError.EmptyEmail ->
                R.string.error_empty_email_validError

            AuthError.InvalidEmail ->
                R.string.error_invalid_email_validError

            AuthError.EmailAlreadyExists ->
                R.string.error_email_already_exists_AuthError
            // password
            AuthError.EmptyPassword ->
                R.string.error_empty_password_validError

            AuthError.WeakPassword ->
                R.string.error_weak_password_AuthError
            // general error
            AuthError.InvalidCredentials ->
                R.string.error_invalid_credentials_AuthError

            AuthError.NetworkError ->
                R.string.error_network_error_AuthError

            AuthError.Unknown ->
                R.string.error_unknown_error_AuthError
        }
    }
}