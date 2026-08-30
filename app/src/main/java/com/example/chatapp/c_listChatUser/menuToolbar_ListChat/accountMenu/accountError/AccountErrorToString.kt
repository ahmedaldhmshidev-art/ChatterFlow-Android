package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError

import com.example.chatapp.R

object AccountErrorToString {
    fun AccountError.toStringRes(): Int {
        return when (this) {
            AccountError.Field.NameBlank ->
                R.string.error_blank_name_profile

//            AccountError.Field.NameMustBeDifferent ->
//                R.string.error_new_name_must_be_different

            AccountError.Field.CurrentPasswordBlank ->
                R.string.error_blank_current_password

            AccountError.Field.NewPasswordBlank ->
                R.string.error_blank_new_password

            AccountError.Field.NewPasswordTooShort ->
                R.string.error_new_password_less_6

            AccountError.Field.NewPasswordMustBeDifferent ->
                R.string.error_new_password_must_be_different

            AccountError.Field.ConfirmPasswordBlank ->
                R.string.error_blank_confirm_password

            AccountError.Field.ConfirmPasswordDoesNotMatch ->
                R.string.error_confirm_password_does_not_match

            AccountError.General.Network ->
                R.string.error_network_error_AuthError

            AccountError.General.InvalidCurrentPassword ->
                R.string.error_invalid_current_password

            AccountError.General.Unknown ->
                R.string.error_unknown_error_AuthError
        }
    }
}