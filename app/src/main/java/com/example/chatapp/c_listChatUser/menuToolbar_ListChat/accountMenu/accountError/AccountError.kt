package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.accountError

sealed interface AccountError {

    sealed interface Field : AccountError {

        data object NameBlank : Field
//        data object NameMustBeDifferent : Field

        data object CurrentPasswordBlank : Field

        data object NewPasswordBlank : Field
        data object NewPasswordTooShort : Field
        data object NewPasswordMustBeDifferent : Field

        data object ConfirmPasswordBlank : Field
        data object ConfirmPasswordDoesNotMatch : Field
    }

    sealed interface General : AccountError {
        data object InvalidCurrentPassword : General
        data object Network : General
        data object Unknown : General
    }
}