package com.example.chatapp.mapper

import com.example.chatapp.e_messageChatId.a_model_msg.StatusMessage

// هذا نظام ادارة الحالات بحيث مايحصل تلخبط او العكس من seen  to delivered
class StatusManager {
    fun changeStatus(
        currentStatusMessage: StatusMessage, // الحالة الحالية لرسالة
        newStatusMessage: StatusMessage      // الحالة المراد تحويلها لها
    ):StatusMessage{
        return when(currentStatusMessage)
        {StatusMessage.PENDING ->{ // 🕐
                if (newStatusMessage == StatusMessage.SENT) newStatusMessage
                else
                    currentStatusMessage
        }
            StatusMessage.SENT ->{ // ✔
                if (
                    newStatusMessage == StatusMessage.DELIVERED ||
                    newStatusMessage == StatusMessage.SEEN
                    )
                    newStatusMessage
                else
                    currentStatusMessage
            }
            StatusMessage.DELIVERED ->{ // ✔✔
                if (newStatusMessage == StatusMessage.SEEN)
                    newStatusMessage
                else
                    currentStatusMessage
            }
            StatusMessage.SEEN ->{   // ✔✔✔
                currentStatusMessage
            }
        }
    }
}