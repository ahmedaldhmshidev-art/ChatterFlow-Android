package com.example.chatapp.e_messageChatId.c_domain.useCase

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.viewModel_msg.SyncPipelineMsg

class DeletedMessageUseCase(
    private val pipelineMsg: SyncPipelineMsg
) {
    suspend operator fun invoke(
        message: MessageText
    ): MessageText {
        val deletedMsg = message.copy(
//            messageText = "🚫  تم حذف هذة الرسالة" ,
            deleted = true
        )
        return pipelineMsg.onMessageEdited(deletedMsg)
    }
}