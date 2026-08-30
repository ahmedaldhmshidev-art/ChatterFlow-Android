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

    suspend fun onMessageEdited(message:MessageText) :MessageText {
        val editeMsg = messageRepository.editeMessage(message = message)
        updateChatDocumentLastMsg.editeIfLastMessage(editeMsg)
        return editeMsg
    }

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
