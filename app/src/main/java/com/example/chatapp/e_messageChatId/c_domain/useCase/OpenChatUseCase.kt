package com.example.chatapp.e_messageChatId.c_domain.useCase

import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.b_user_list.repository_user_list.RepositoryUserList
import com.example.chatapp.e_messageChatId.b_data.repository_msg.MessageRepository
import com.example.chatapp.e_messageChatId.c_domain.seen.SeenMsgUseCase
import com.example.chatapp.e_messageChatId.c_domain.unreadCount.ResetUnreadUseCase


class OpenChatUseCase(
    private val userRepository: RepositoryUserList,
    private val msgRepository: MessageRepository,
    private val seenMsgUseCase: SeenMsgUseCase,
    private val resetUnreadUseCase: ResetUnreadUseCase
) {

    suspend operator fun invoke(
        chatId:String , currentUserId:String , otherUserId:String,
    ):User{
        // تحميل بيانات المستخدم الاخر
        val otherUser = userRepository.getUserById(otherUserId)

        // تحديث الرسائل القديمة ال seen
        val unreadMessage = msgRepository.getUnreadMessages(
            chatId = chatId ,
            currentUserId = currentUserId
        )
        unreadMessage.forEach {
            message ->
            seenMsgUseCase(
                message = message
            )
        }
        if (unreadMessage.isNotEmpty()){
            resetUnreadUseCase(
                chatId = chatId,
                currentUserId = currentUserId
            )
        }
        // اعادة المستخدم الاخر
        return otherUser?:User()
    }
}