package com.example.chatapp.e_messageChatId.c_domain.delivered

import android.util.Log
import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.e_messageChatId.a_model_msg.MessageText
import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.DocumentChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeliveredObserver (
    private val fireStore: FirebaseFirestore,
    private val scope: CoroutineScope,
    private val sessionManager: SessionManager,
    private val deliveredMsgUseCase: DeliveredMsgUseCase,
    ) {
    private var registration: ListenerRegistration? = null // سجل المستمع اي المراقب
    fun start() {
//        Log.d("DeliveredObserver","DeliveredObserver _ Start")
        stop()
        val currentUserId = sessionManager.currentUserId ?: return  // uid المستخدم الحالي
        registration = fireStore
            .collectionGroup("messages")
            .whereEqualTo("receiverId", currentUserId) // جميع الرسايل ماعدا رسال المستخدم الحالي
            .addSnapshotListener {
                                 snapshot, error ->

                if (error != null || snapshot == null) {
                    Log.e("DeliveredObserver", " listenerError", error)
                    return@addSnapshotListener
                }
                snapshot.documentChanges.forEach {
                    change ->
                    if(change.type!= DocumentChange.Type .ADDED &&
                        change.type != DocumentChange.Type.MODIFIED
                        ) return@forEach

                    val msg = change.document.toObject(MessageText::class.java)

                    if (msg.statusMessage != StatusMessage.SENT) return@forEach

                    if (msg.receiverId!=currentUserId)return@forEach
                    scope.launch {
                        Log.d("DeliveredObserver","DeliveredObserver _ coordinator reached scope")
                        deliveredMsgUseCase(message = msg )
                    }
                }
            }
    }
    fun stop() {
        Log.d("DeliveredObserver","DeliveredObserver _ Stop")
        registration?.remove()
        registration = null
    }
}
