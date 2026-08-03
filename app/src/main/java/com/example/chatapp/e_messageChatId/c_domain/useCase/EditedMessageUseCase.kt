package com.example.chatapp.e_messageChatId.c_domain.useCase

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.viewModel_msg.SyncPipelineMsg

class EditedMessageUseCase(
    private val pipelineMsg: SyncPipelineMsg
) {
    suspend operator fun invoke (
        message:MessageText , newText:String,

    ):MessageText {
        require(newText .isNotBlank())

        val editeMsg = message .copy(
            messageText = newText.trim() ,
            edited = true ,
            editedAt = System.currentTimeMillis()
        )
        return pipelineMsg.onMessageEdited(editeMsg)
    }
}