package com.example.chatapp.utils

object ChatUtilsUid {
    fun generateChatId(
        currentUserUid:String , //  uid المعرف الوحيد للمستخدم الحالي
        otherUserUid:String     // //  uid المعرف الوحيد للمستخدم الاخر
    ):String   // يتم دمج العنصرين كعنصر واحد يصبح هو المعرف الوحيد لدردشة بينهم او اسم الغرفة الي تتخزن رسايلهم فييها
    {
      return listOf(currentUserUid ,otherUserUid)
        .sorted()  // دالة تعمل علي دمج عناصر الlist كعنصر واحد
        .joinToString("_")  // دالة تضيف بين عنصرين _

    }
}