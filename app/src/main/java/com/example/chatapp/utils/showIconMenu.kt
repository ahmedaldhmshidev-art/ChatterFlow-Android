package com.example.chatapp.utils

import android.view.Menu

     fun showIconMenu(menu: Menu) {
        try {
            val field = menu.javaClass.getDeclaredField("mPopup")
            field.isAccessible = true
            val menuPopupHelper = field.get(menu)
            val method = menuPopupHelper.javaClass
                .getDeclaredMethod("setForceShowIcon" ,Boolean::class.java)
            method.isAccessible = true
            method.invoke(menuPopupHelper , true)
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
