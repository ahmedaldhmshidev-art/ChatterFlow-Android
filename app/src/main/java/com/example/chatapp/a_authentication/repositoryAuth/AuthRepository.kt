package com.example.chatapp.a_authentication.repositoryAuth

import com.example.chatapp.a_authentication.modelAuth.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    // create account
    suspend fun registerUser(
        name: String, email: String, password: String
    ) {
        val result = auth.createUserWithEmailAndPassword(email, password)
            .await()

        // save user to fireStore
        val uid = result.user?.uid
            ?: throw IllegalStateException("Firebase user is null after registration") // uid المستخدم الحالي

        //object
        val user = User(uid = uid, name = name, email = email, bio = "Hello I new to app")

        firestore.collection("users")
            .document(uid)
            .set(user)
            .await()
    }

    // login using account
    suspend fun signUser(
        email: String, password: String
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .await()
    }


    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val userCurrent =
                auth.currentUser ?: return Result.failure(
                    IllegalStateException("User not logged in")
                )

            val email = userCurrent.email ?: return Result.failure(
                IllegalStateException("Email not found")
            )
            // بيانات اعتماد المستخدم عليها لاثبات هويته
            val credential = EmailAuthProvider
                .getCredential(email, currentPassword)
            // دالة اعادة التحقق من هوية المستخدم اذا كلشي صحصح اكمل الاجرائات
            userCurrent.reauthenticate(credential)
                .await()
            // اذا السابق صح ارسل امر تغير كلمة المرر
            userCurrent.updatePassword(newPassword)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

