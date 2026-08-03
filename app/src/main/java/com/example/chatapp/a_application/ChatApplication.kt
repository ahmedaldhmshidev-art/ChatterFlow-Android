package com.example.chatapp.a_application

import android.app.Application

class ChatApplication: Application() {
    //هذا كلاس يعيش طول عمر التطبيق
//    Application()  هذا اول كلاس يعمل عند تشغيل التطبيق قبل كل شي
   lateinit var appContainer: AppContainer
    override fun onCreate() { // دالة تستدعا مرة واحدة فقط وليس كل مرة تفتح الشاشة
        super.onCreate()      // تقول نفذ كود اندرويد الاساسي اولا

        appContainer = AppContainer()

    }
}