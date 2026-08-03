package com.example.chatapp.e_messageChatId.stateAndEvent_msg


sealed interface SendState {
    data object Idle    :SendState
    data object Sending :SendState
    data object Success :SendState
}