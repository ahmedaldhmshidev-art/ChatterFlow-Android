package com.example.chatapp.e_messageChatId.a_model_msg

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MessageText(
    val chatId:String = "",
    val messageId    :String ="",
    val senderId     :String ="",
    val receiverId   :String ="",

    val messageText  :String ="",
    val timestamp    :Long   =0L,
    val statusMessage: StatusMessage = StatusMessage.PENDING,
    val typeMessage  : TypeMessage     = TypeMessage.TEXT ,

    val edited :Boolean = false ,
    val deleted:Boolean = false ,
    val editedAt :Long = 0L,

): Parcelable

enum class StatusMessage {
    PENDING,
    SENT,
    DELIVERED,
    SEEN;
companion object
{
 fun from(value1 :String?):StatusMessage{
        return try {
            valueOf(value = value1?:"PENDING")
        }
        catch (e:Exception){
            PENDING
        }
    }
}
}


enum class TypeMessage {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO
}
enum class ChatStatus{
    ACTIVE , ARCHIVED , BLOCKED
}
