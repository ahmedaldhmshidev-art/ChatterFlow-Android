package com.example.chatapp.mapper

import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage

data class StatusUi(
    val icon:String,
    val color:Int
)

object MapperStatusMessageToMark {
    fun mapToMark(status:StatusMessage):String{
        return when(status){
            StatusMessage.PENDING->
                "🕐"
            StatusMessage.SENT ->
                "✔"
            StatusMessage.DELIVERED ->
                "✔✔"
            StatusMessage.SEEN->
                "✔✔✔"
        }
    }
}