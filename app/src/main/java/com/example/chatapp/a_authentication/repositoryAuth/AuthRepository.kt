package com.example.chatapp.a_authentication.repositoryAuth

import com.example.chatapp.a_authentication.SessionManager
import com.example.chatapp.a_authentication.modelAuth.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth : FirebaseAuth,
    private val firestore : FirebaseFirestore,
    private val sessionManager: SessionManager,

    ){
    suspend fun registerUser(
        name:String, email:String, password:String
    ){
        auth.createUserWithEmailAndPassword(email ,password).await()

        // save user to fireStore
        val uid = sessionManager.currentUserId ?: return  // uid المستخدم الحالي

        //object
        val user = User(uid = uid , name = name , email = email , bio = "Hello I new to app")
        firestore.collection("users").document(uid).set(user).await()
    }


    suspend fun signUser(
        email: String , password: String
    ){
        auth.signInWithEmailAndPassword(email , password).await()
    }
}

