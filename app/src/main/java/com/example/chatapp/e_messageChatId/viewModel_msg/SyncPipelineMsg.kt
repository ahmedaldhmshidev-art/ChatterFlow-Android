package com.example.chatapp.e_messageChatId.viewModel_msg

import com.example.chatapp.mapper.StatusManager
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.b_data.repository_msg.MessageRepository
import com.example.chatapp.e_messageChatId.b_data.repository_msg.SendMessageBatch
import com.example.chatapp.e_messageChatId.b_data.repository_msg.UpdateChatDocumentLastMsg

class SyncPipelineMsg(
    private val messageRepository: MessageRepository,
    private val updateChatDocumentLastMsg: UpdateChatDocumentLastMsg,
    private val statusManager: StatusManager,
    private val sendMessageBatch: SendMessageBatch
) {
    suspend fun onMessageSent(message: MessageText): MessageText {

        return sendMessageBatch.send(message)

    }

    suspend fun onMessageEdited(message:MessageText):MessageText {
        // edited to messageText
        val editeMsg = messageRepository.editeMessage(message = message)
            ?: throw
            IllegalArgumentException("Error to Edited message on Pipeline")
        // edited to LastMessage
        updateChatDocumentLastMsg.editeIfLastMessage(editeMsg)

        return editeMsg
    }


//
//        val saveMsg = messageRepository.insert(message)
//            ?: throw IllegalArgumentException("msg ge not saved")
//
//        val newStatus = statusManager.changeStatus(
//            currentStatusMessage = saveMsg.statusMessage,
//            newStatusMessage = StatusMessage.SENT
//        )
//        val sentMsg = saveMsg.copy(statusMessage = newStatus)
//
//        messageRepository.updateMsgStatusOnly(
//            chatId = sentMsg.chatId,
//            messageId = sentMsg.messageId,
//            status = sentMsg.statusMessage
//        )
//        updateChatDocumentLastMsg.toLastMessage(sentMsg)
//
//        increaseUnreadUseCase(sentMsg)
//
//        return sentMsg
//    }


    suspend fun onStatusChanged(
        message: MessageText,
        newStatus: StatusMessage,
    ): MessageText {

        val nStatus = statusManager.changeStatus(
            currentStatusMessage = message.statusMessage,
            newStatusMessage = newStatus,
        )
        if (nStatus == message.statusMessage) return message

        val updateMsg = message.copy(statusMessage = nStatus)

        messageRepository.updateMsgStatusOnly(
            chatId = updateMsg.chatId,
            messageId = updateMsg.messageId,
            status = updateMsg.statusMessage,
        )
        updateChatDocumentLastMsg.toStatusOnly(updateMsg)

        return updateMsg
    }
}

//    suspend fun onMessagesSeen(
//        chatId: String, currentUserId: String
//    ) {
//        try {
//            val unreadMsg = messageRepository.getUnreadMessages(
//                chatId = chatId,
//                currentUserId = currentUserId
//            )
//            if (unreadMsg.isEmpty()) return
//
//            unreadMsg.forEach { msg ->
//
//                val newStatus = statusManager.changeStatus(
//                    currentStatusMessage = msg.statusMessage,
//                    newStatusMessage = StatusMessage.SEEN
//                )
//                if (newStatus == msg.statusMessage) {
//                    return@forEach
//                }
//                val copyMsg = msg.copy(
//                    statusMessage = newStatus
//                )
//                messageRepository.updateMsgStatusOnly(
//                    chatId = copyMsg.chatId,
//                    messageId = copyMsg.messageId,
//                    status = copyMsg.statusMessage
//                )
//            }
//
//            val lastMsg = unreadMsg.maxByOrNull {
//                it.timestamp
//            }
//            lastMsg?.let {
//                val nStatus = statusManager.changeStatus(
//                    currentStatusMessage = it.statusMessage,
//                    newStatusMessage = StatusMessage.SEEN
//                )
//                if (nStatus != it.statusMessage) {
//                    val copyMsg = it.copy(
//                        statusMessage = nStatus
//                    )
//                    updateChatDocumentLastMsg.toStatusOnly(
//                        message = copyMsg
//                    )
//                }
//            }
//            unreadManager.resetUnread(
//                chatId = chatId, currentUserId = currentUserId
//            )
//        }
//        catch (e:Exception){
//            Log.e("onMessagesSeen","onMessagesSeen: Failed to mark messages as seen for chat : $chatId" , e)
//        }
//
//    }
//
//}
