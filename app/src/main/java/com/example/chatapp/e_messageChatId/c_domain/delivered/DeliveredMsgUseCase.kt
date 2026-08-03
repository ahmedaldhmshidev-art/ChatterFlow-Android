package com.example.chatapp.e_messageChatId.c_domain.delivered

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.viewModel_msg.SyncPipelineMsg

class DeliveredMsgUseCase(
    private val pipelineMsg: SyncPipelineMsg,
) {
    suspend operator fun invoke(
        message :MessageText
    ):MessageText {
        return pipelineMsg.onStatusChanged(
            message = message ,
            StatusMessage.DELIVERED,
        )
    }
}