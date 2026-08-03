package com.example.chatapp.e_messageChatId.c_domain.unreadCount

import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.tasks.await

class UnreadManager(private val firestore: FirebaseFirestore) {

    fun addIncreaseUnreadToBatch(
        batch : WriteBatch,
        message: MessageText
    ){
        val chatRef = firestore
            .collection("chats")
            .document(message.chatId)

        batch.update(chatRef ,
            "unreadCountMap.${message.receiverId}",FieldValue.increment(1)
        )
    }




     // زيادة العداد ب 1
    suspend fun increaseUnread(message:MessageText) {

        val ref = firestore
            .collection("chats")
            .document(message.chatId)

        firestore.runTransaction {
            transaction ->
            val snapshot = transaction.get(ref) // جلب كل ما في الشات من عناصر
            val currentUnread = snapshot.get("unreadCountMap") as? Map<String, Long>
                ?: emptyMap() // جلب عنصر واحد الذي هو العداد

            val update = currentUnread.toMutableMap() // حلوة الي قابل لتعديل من اجل نعدل علية
            update[message.receiverId] = (update[message.receiverId] ?: 0L) + 1L
            transaction.update(ref, "unreadCountMap", update)
        }.await()
    }


    // تصفير العداد
    suspend fun resetUnread(
        chatId: String, currentUserId : String)
    {
        firestore.collection("chats").document(chatId)
            .update("unreadCountMap.$currentUserId", 0).await()
    }
}