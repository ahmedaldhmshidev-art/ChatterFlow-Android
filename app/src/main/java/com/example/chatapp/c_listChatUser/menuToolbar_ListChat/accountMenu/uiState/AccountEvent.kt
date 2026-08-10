package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState


sealed class AccountEvent {
    data object SaveSuccessEditProfile : AccountEvent()

    data class Error(val message: String) : AccountEvent()

    data class ErrorProfile(val field: ProfileField) : AccountEvent()

    data class ErrorPassword(val field: PasswordField) : AccountEvent()

    data object PasswordChangedSuccess : AccountEvent()

    data object NavigationToLogin : AccountEvent()
}

enum class PasswordField {
    CURRENT_PASSWORD_BLANK,
    NEW_PASSWORD_BLANK,
    CONFIRM_PASSWORD_BLANK,
    NEW_PASSWORD_TOO_SHORT_6,

    NEW_PASSWORD_MUST_BE_DIFFERENT_CURRENT,
    CONFIRM_PASSWORD_DOES_NOT_MATCH,

}

enum class ProfileField {
    NAME_BLANK,
    NEW_NAME_MUST_BE_DIFFERENT,
}