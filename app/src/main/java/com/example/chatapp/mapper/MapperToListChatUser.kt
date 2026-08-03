package com.example.chatapp.mapper

import com.example.chatapp.a_authentication.modelAuth.User
import com.example.chatapp.c_listChatUser.modelList.ListChatUsers
import com.example.chatapp.d_chat_Document.model_document.ChatDocument
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.a_model_msg.TypeMessage


    fun ChatDocument.toListChatUsers(
        user: User, currentUserId: String
    ): ListChatUsers {

        val otherUserId = participants.first { it != currentUserId }

        return ListChatUsers(
            chatId = chatId,
            userId = otherUserId,
            userName = user.name,
            userImg = user.imageUri,

            lastMessage = lastMessage?.lastMessageText ?: "",
            timeLastMessage = lastMessage?.timeLastMessage ?: 0L,
            unreadCountMap = unreadCountMap[currentUserId] ?: 0,

            lastMessageSenderId = lastMessage?.senderIdLastMessage ?: "",
            statusMessage = lastMessage?.statusLastMessage ?:StatusMessage.SENT,

//            StatusMessage.from(
//                lastMessage?.statusLastMessage ?: StatusMessage.SENT.name
//            ),
            typeMessage = lastMessage?.typeLastMessage ?: TypeMessage.TEXT,
            typingUsers = typingUsers[otherUserId] == true,
        )
    }


//    fun toChatListItem(
//        chatDocument: ChatDocument,
//        user: User,
//        otherUserId:String,
//        currentId :String
//    ):ListChatUsers{
//        return ListChatUsers(
//            chatId = chatDocument.chatId,
//            userId = otherUserId ,
//            userName = user.name ,
//            userImg = user.imageUri,
//
//
//            lastMessage = chatDocument.lastMessage?.lastMessageText,
//
//            timeLastMessage = chatDocument.lastMessage?.timeLastMessage ,
//
//            statusMessage =StatusMessage.from(chatDocument.lastMessage?.statusLastMessage) ,
//
//            unreadCountMap = chatDocument.unreadCountMap[currentId]?:0,
//
//            lastMessageSenderId = chatDocument.lastMessage?.senderIdLastMessage
//
//        )
//    }
//

