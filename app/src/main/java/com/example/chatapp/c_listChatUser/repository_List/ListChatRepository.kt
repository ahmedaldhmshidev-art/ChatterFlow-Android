package com.example.chatapp.c_listChatUser.repository_List

import com.example.chatapp.d_chat_Document.model_document.ChatDocument
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ListChatRepository (private val firestore:FirebaseFirestore){

    fun getAllChat(currentId:String): Flow<List<ChatDocument>> = callbackFlow {
        val listener = firestore
            .collection("chats")
            .whereArrayContains("participants", currentId)
            .addSnapshotListener {
                    snapshot, error ->

                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val chat = snapshot?.documents?.mapNotNull {
                    it.toObject(ChatDocument::class.java)
                } ?: emptyList()

                val order = chat.sortedByDescending { it.lastMessage?.timeLastMessage}

                trySend(order)
            }

        awaitClose {
            listener.remove()
        }
    }
    suspend fun deleteChat(chatId:String):Result<Unit>{
        return try {
            val chatRef = firestore
                .collection("chats")
                .document(chatId)

            val messagesSnapshot = chatRef
                .collection("messages")
                .get()
                .await()

            val batch = firestore.batch()
            // مر عل جميع الرسائل وحذفها
            for (document in messagesSnapshot.documents){
                batch.delete(document.reference)
            }
            // حذف الdocument بعد حذف جميع الرسائل تبعه
            batch.delete(chatRef)
            // نفذ الحذف
            batch.commit().await()

            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}
