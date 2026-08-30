package com.example.chatapp.e_messageChatId.b_data.repository_msg

import com.example.chatapp.d_chat_Document.model_document.ChatDocument
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.tasks.await

class UpdateChatDocumentLastMsg(private val firestore: FirebaseFirestore) {

     fun toLastMessage(
        message: MessageText,
        batch: WriteBatch
    ) {
        val chatRef = firestore
            .collection("chats")
            .document(message.chatId)

        batch.update(chatRef,
            mapOf(
                "lastMessage.lastMessageId" to message.messageId,
                "lastMessage.lastMessageText" to message.messageText,
                "lastMessage.timeLastMessage" to message.timestamp,
                "lastMessage.typeLastMessage" to message.typeMessage,
                "lastMessage.senderIdLastMessage" to message.senderId,
                "lastMessage.statusLastMessage" to message.statusMessage,
            )
        )
    }


    suspend fun toStatusOnly(
        message: MessageText
    ){
       val chatRef = firestore.collection("chats")
            .document(message.chatId)
            val snapshot = chatRef.get()
                .await()

        val chatDocument = snapshot.toObject(ChatDocument::class.java)?:throw IllegalStateException()

        val lastMsg = chatDocument.lastMessage ?: return

        if (lastMsg.lastMessageId != message.messageId) { // اذا كانت الرسالة ليس اخر رسالة لا تحدث حالتها
            return
        }
        chatRef.update(
            "lastMessage.statusLastMessage" , message.statusMessage
        ).await()
    }

    suspend fun editeIfLastMessage(message:MessageText){
        val chatRef = firestore
            .collection("chats")
            .document(message.chatId)

        val snapshot = chatRef.get().await()

        val chatDocument = snapshot.toObject(ChatDocument::class.java) ?:throw IllegalStateException()

        val lastMsg = chatDocument.lastMessage ?: return

        if (lastMsg.lastMessageId != message.messageId) return

        chatRef.update(
            mapOf(
                "lastMessage.lastMessageText" to message.messageText,
                "lastMessage.typeLastMessage" to message.typeMessage,
                "lastMessage.statusLastMessage" to message.statusMessage,
            )
        ).await()
    }

    suspend fun removeLastMessage(chatId:String){
        firestore
            .collection("chats")
            .document(chatId)
            .update(
                mapOf("lastMessage" to null)
            )
            .await()
    }
}