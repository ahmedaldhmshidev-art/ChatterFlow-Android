package com.example.chatapp.e_messageChatId.Error


sealed interface MessageError {
    data object PermissionDenied : MessageError
    data object Offline : MessageError
    data object NotFound : MessageError
    data object FailedPrecondition : MessageError
    data object Aborted : MessageError
    data object DeadlineExceeded : MessageError
    data object Authentication : MessageError
    data object Network : MessageError
    data object Unknown : MessageError
}
