package com.example.chatapp.e_messageChatId.c_domain.seen

import android.util.Log
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.example.chatapp.e_messageChatId.c_domain.unreadCount.ResetUnreadUseCase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SeenObserver(
    private val fireStore: FirebaseFirestore,
    private val scope: CoroutineScope,
    private val sessionManager: SessionManager,
    private val seenMsgUseCase: SeenMsgUseCase,
    private val resetUnreadUseCase: ResetUnreadUseCase
) {
    private var listener: ListenerRegistration? = null
    fun start(chatId: String) {
        stop()
        listener = fireStore
            .collection("chats")
            .document(chatId)
            .collection("messages")
            .whereEqualTo("chatId", chatId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                snapshot.documentChanges.forEach { change ->

                    if (change.type != DocumentChange.Type.ADDED && change.type != DocumentChange.Type.MODIFIED) return@forEach

                    val msg = change.document.toObject(MessageText::class.java)

                    Log.d(
                        "SeenTestMsgId",
                        "chatId=${msg.chatId} msgId= ${msg.messageId} status=${msg.statusMessage}"
                    )
                    handleUpdatedMessage(msg)
                }
            }
    }

    private fun handleUpdatedMessage(msg: MessageText) {

        val currentUserId = sessionManager.currentUserId ?: return

        if (msg.senderId == currentUserId) return // الرسالة ليست لي
        if (msg.receiverId != currentUserId) return // الرسالة انا المستقبل
        if (msg.statusMessage == StatusMessage.SEEN) return

        scope.launch {
            seenMsgUseCase(
                message = msg,
            )
            resetUnreadUseCase(
                chatId = msg.chatId,
                currentUserId
            )
        }
    }

    fun stop() {
        listener?.remove()
        listener = null
    }

    fun destroy() {
        stop()
//        scope.cancel()
    }

}