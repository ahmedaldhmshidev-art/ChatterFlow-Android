package com.example.chatapp.a_authentication

import com.google.firebase.auth.FirebaseAuth

class SessionManager(private val firebaseAuth: FirebaseAuth) {
    val currentUserId :String? get() = firebaseAuth . currentUser?.uid

    val isLoggedIn:Boolean get() = firebaseAuth.currentUser != null

    fun logout(){
        firebaseAuth.signOut()
    }
}