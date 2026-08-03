package com.example.chatapp.a_application

import android.app.Activity
import android.content.Context

val Context.appContainer : AppContainer
    get() = (applicationContext as ChatApplication).appContainer

val Activity.appContainer :AppContainer
    get() = (applicationContext as ChatApplication).appContainer
