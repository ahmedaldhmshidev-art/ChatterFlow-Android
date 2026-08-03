package com.example.chatapp.e_messageChatId.c_domain.unreadCount

class ResetUnreadUseCase(
    private val unreadManager: UnreadManager,
    ) {

    suspend operator fun invoke(
        chatId:String ,
        currentUserId:String
    ){
        unreadManager.resetUnread(
            chatId= chatId,
            currentUserId=currentUserId
        )
    }
}