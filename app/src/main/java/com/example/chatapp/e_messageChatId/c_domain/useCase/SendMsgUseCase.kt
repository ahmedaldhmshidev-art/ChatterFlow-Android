package com.example.chatapp.e_messageChatId.c_domain.useCase

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.viewModel_msg.SyncPipelineMsg

class SendMsgUseCase(
    private val pipelineMsg: SyncPipelineMsg,
) {
    suspend operator fun invoke (
        chatId:String ,
        senderId:String ,
        receiverId:String ,
        msgText:String,
    ):MessageText{

        validate(
            chatId,
            senderId,
            receiverId,
            msgText,
        )
        val message = createMessage(
            chatId ,
            senderId,
            receiverId,
            msgText,
            )

        return  pipelineMsg.onMessageSent(message)

    }



    private fun validate(
        chatId: String,
        senderId: String,
        receiverId: String,
        msgText: String
    ) {
        require(chatId .isNotBlank()){"ChatId is empty"}
        require(senderId .isNotBlank()){"SenderId is empty"}
        require(receiverId .isNotBlank()){"Receiver is empty"}
        require(msgText.isNotBlank()){"Message text is empty"}
    }

    private fun createMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        msgText: String
    ): MessageText {
        return MessageText(
            chatId = chatId ,
            senderId = senderId,
            receiverId = receiverId,
            messageText = msgText.trim(),
            timestamp = System.currentTimeMillis(),
            statusMessage = StatusMessage.PENDING,
            edited = false,
            deleted = false,
            )
    }
}