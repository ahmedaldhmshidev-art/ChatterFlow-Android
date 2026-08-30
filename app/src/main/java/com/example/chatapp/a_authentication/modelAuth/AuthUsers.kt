package com.example.chatapp.a_authentication.modelAuth

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
//    val password:String ="",
    val imageUri: String = "",
    val bio: String = "",
)