package com.example.chatapp.e_messageChatId.b_data.repository_msg

import android.util.Log
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepository(private val firebaseStore : FirebaseFirestore) {

    suspend fun updateMsgStatusOnly(
        chatId: String , messageId:String , status:StatusMessage
    ){
        if (messageId .isBlank()) return

      firebaseStore
          .collection("chats")
          .document(chatId)
          .collection("messages")
          .document(messageId)
          .update(
              "statusMessage" , status
          ).await()
    }
    // تعديل الرسالة كامل عند edite
    suspend fun editeMessage(message: MessageText
    ):MessageText? {
        Log.d("editeMessageRepository","editeMessageRepositoryIsDelete:${message.deleted}, IsEdite:${message.edited}")
        return try {
            firebaseStore
                .collection("chats")
                .document(message.chatId)
                .collection("messages")
                .document(message.messageId)
                .set(message)
                .await()
            message
        }
        catch (e:Exception){
            Log.e("Error Edite message ! ","failed:" ,e)
            null
        }
    }



// هذه دالة تجلب جميع الرسال الغير مقروئة  من اجل اول مانفتح الشات يتم توحيل حالة الرسال الي seen
    suspend fun getUnreadMessages(
    chatId: String, currentUserId:String
    ):List<MessageText> {
        return try {
           firebaseStore
                .collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING) // ترتيب الرسال حسب الوقت
                .whereEqualTo("chatId", chatId)
                .whereEqualTo("receiverId", currentUserId)
                .get().await()
               .documents.mapNotNull {
                    it.toObject(MessageText::class.java)
                }
                .filter {
                    it.statusMessage != StatusMessage.SEEN
                }
        }
        catch (e: Exception) {
            emptyList()
        }
    }

    fun getMessage(
        chatId: String
    ): Flow<List<MessageText>>
    = callbackFlow {
        val listener = firebaseStore
            .collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING) // ترتيب الرسال حسب الوقت
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull {
                    it.toObject(MessageText::class.java)
                } ?: emptyList()
                Log.d("getMessageTestMsgId","msgRepository_getAllMessage=${messages} ")
                trySend(messages) }
        awaitClose {
            listener.remove()
        }
    }
    suspend fun removeAllMessage(chatId: String){
        val snapshot = firebaseStore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .get()
            .await()

        val batch = firebaseStore.batch()

        snapshot.documents.forEach { document ->
            batch.delete(document.reference)
        }
        batch.commit().await()
    }


//    suspend fun insert(messageText: MessageText):MessageText ? {
//        val messageIdRef = firebaseStore
//            .collection("chats")
//            .document(messageText.chatId)
//            .collection("messages")
//            .document()  // توليد ID الرسالة
//
//        val messageWithId = messageText.copy(
//            messageId = messageIdRef.id,
//            )
//        return try {
//            messageIdRef.set(messageWithId).await()
//            messageWithId
//         }
//        catch (e: Exception) {
//            Log.e("Save_Error_insertRepo","insertError",e)
//            null
//        }
//    }

//    suspend fun updateAllMsg(
//        message: MessageText
//    ) {
//        firebaseStore
//            .collection("chats")
//            .document(message.chatId)
//            .collection("messages")
//            .document(message.messageId)
//            .set(message)
//            .await()
//    }

}