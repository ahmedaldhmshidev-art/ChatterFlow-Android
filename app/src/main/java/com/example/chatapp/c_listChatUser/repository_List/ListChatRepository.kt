package com.example.chatapp.c_listChatUser.repository_List

import com.example.chatapp.d_chat_Document.model_document.ChatDocument
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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





//    // Typing
//    suspend fun updateTyping(chatId: String , userId:String ,isTyping:Boolean) {
//        firestore.collection("chats")
//            .document(chatId).set(
//                mapOf("typingWright" to mapOf(
//                    userId to mapOf(
//                        "isTyping" to isTyping,
//                        "timestamp" to System.currentTimeMillis()
//                    )))
//                , SetOptions.merge()
//            ).await()
//    }
//
//    fun getListenerTyping(chatId: String, otherUserId:String, isTypingResult:(Boolean)->Unit ){
//        firestore.collection("chats")
//            .document(chatId)
//            .addSnapshotListener { snapshot, _ ->
//
//                val typingMap = snapshot?.get("typingWright") as? Map<String,Any> ?: emptyMap()
//
//                val userTyping = typingMap[otherUserId] as? Map<String,Any>
//
//                val isTyping = userTyping?.get("isTyping") as? Boolean ?: false
//
//                isTypingResult(isTyping)
//            }
//    }
}
