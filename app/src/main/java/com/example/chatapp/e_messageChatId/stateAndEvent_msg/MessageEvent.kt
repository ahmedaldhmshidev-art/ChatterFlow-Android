package com.example.chatapp.e_messageChatId.stateAndEvent_msg

import com.example.chatapp.e_messageChatId.Error.MessageError

sealed interface MessageEvent {
    data class ShowError(val messageError: MessageError) : MessageEvent

    data object EditSuccess : MessageEvent
    data object DeleteSuccess : MessageEvent
    data object RemoveAllMessageSuccess : MessageEvent

    data class CopyMessage(val copyText: String) : MessageEvent
}