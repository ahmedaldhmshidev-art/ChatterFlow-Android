package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.accountMenu.uiState


sealed class AccountEvent {
    data object ProfileUpdated : AccountEvent()
    data object PasswordChanged : AccountEvent()
    data object NoChangedProfile : AccountEvent()
    data object NavigationToLogin : AccountEvent()
}
