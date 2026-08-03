package com.example.chatapp.d_chat_Document.repository_document

import com.example.chatapp.d_chat_Document.model_document.ChatDocument
import com.example.chatapp.e_messageChatId.a_model_msg.ChatStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatDocumentRepository (
    private val firestore :FirebaseFirestore)
{
    suspend fun createChatDocumentIfNotExist(
        chatId: String , participants :List<String>
    ) {
        val ref = firestore
            .collection("chats")
            .document(chatId)

        val snapshot = ref.get().await()

        if (!snapshot.exists()) {

            val newChatDocument = ChatDocument(
                chatId = chatId,
                participants = participants,
                createdAt = System.currentTimeMillis(),
                lastMessage = null,
                typingUsers = participants.associateWith { false },
                unreadCountMap = participants.associateWith { 0 },
                chatStatus = ChatStatus.ACTIVE
            )
            ref.set(newChatDocument ).await()
        }
    }


    fun getAllChatDocument(chatId:String):Flow<ChatDocument> = callbackFlow{
        val listener = firestore
            .collection("chats")
            .document(chatId)
            .addSnapshotListener{
                snapshot , error ->
                if (error != null){
                    close(error)
                    return@addSnapshotListener
                }
                val chats = snapshot?.toObject(ChatDocument::class.java)
                if (chats != null){
                    trySend(chats)
                }
            }
        awaitClose {
            listener.remove()
        }
    }

    suspend fun updateTyping(chatId: String , userId:String , isTyping:Boolean){
        firestore . collection("chats")
            .document(chatId)
            .update(
            "typingUsers.$userId" , isTyping
        ).await()
    }

}