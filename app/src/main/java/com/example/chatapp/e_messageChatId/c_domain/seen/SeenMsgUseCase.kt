package com.example.chatapp.e_messageChatId.c_domain.seen

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.viewModel_msg.SyncPipelineMsg

class SeenMsgUseCase(
   private val pipelineMsg: SyncPipelineMsg,
) {
    suspend operator fun invoke(
        message: MessageText ,
    ):MessageText{

        if (message.statusMessage == StatusMessage.SEEN) return message

       val updateMsg = pipelineMsg.onStatusChanged(
            message   = message,
            newStatus = StatusMessage.SEEN,
           )
        return updateMsg
    }
}