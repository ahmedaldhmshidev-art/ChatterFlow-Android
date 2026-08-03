package com.example.chatapp.e_messageChatId.b_data.repository_msg

import android.util.Log
import com.example.chatapp.e_messageChatId.c_domain.unreadCount.UnreadManager
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SendMessageBatch(
    private val firestore : FirebaseFirestore,
    private val chatDocumentLastMsg: UpdateChatDocumentLastMsg,
    private val unreadManager: UnreadManager
) {
    suspend fun send (
        message:MessageText
    ):MessageText{
        val messageRef = firestore
            .collection("chats")
            .document(message.chatId)
            .collection("messages")
            .document()

        val sentMessageWithId = message.copy(
            messageId = messageRef.id,
            statusMessage = StatusMessage.SENT
        )
        val batch = firestore.batch()

        // save
        batch.set(messageRef,
            sentMessageWithId
        )
        Log.d("sendMessageBatch","sendMessageBatchIsDelete:${sentMessageWithId.deleted}")
        // تحديث اخر رسالة
        chatDocumentLastMsg.toLastMessage(
            batch = batch,
            message=sentMessageWithId ,
        )
        // زيادة العداد
        unreadManager.addIncreaseUnreadToBatch(
            batch = batch ,
            message = sentMessageWithId
        )
        batch.commit().await()

        return sentMessageWithId
    }
}