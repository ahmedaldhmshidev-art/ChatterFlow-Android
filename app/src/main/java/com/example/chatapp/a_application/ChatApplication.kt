package com.example.chatapp.a_application

import android.app.Application
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.setting.AppSetting
import com.example.chatapp.c_listChatUser.menuToolbar_ListChat.setting.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ChatApplication : Application() {


    //هذا كلاس يعيش طول عمر التطبيق
//    Application()  هذا اول كلاس يعمل عند تشغيل التطبيق قبل كل شي
    lateinit var appContainer: AppContainer

    override fun onCreate() { // دالة تستدعا مرة واحدة فقط وليس كل مرة تفتح الشاشة
        super.onCreate()      // تقول نفذ كود اندرويد الاساسي اولا

        appContainer = AppContainer(this)
        applySavedSettings()
    }

    private fun applySavedSettings() {
        val preferencesManager = appContainer.preferencesManager

        val language = runBlocking { preferencesManager.language.first() }
        val theme = runBlocking { preferencesManager.theme.first() }

        AppSetting.applyLanguage(language)
        AppSetting.applyTheme(theme)
    }
}