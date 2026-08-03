package com.example.chatapp.b_user_list.repository_user_list

import android.util.Log
import com.example.chatapp.a_authentication.modelAuth.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RepositoryUserList(private val firestore: FirebaseFirestore) {
    fun getAllUser(currentUserId: String): Flow<List<User>> = callbackFlow {

        val listener = firestore
            .collection("users")
            .addSnapshotListener{
                    user, error ->

            if (error != null ) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val users = user?.documents?.mapNotNull {
                it.toObject(User::class.java)
            }
                ?.filter { it.uid != currentUserId } ?: emptyList()

            trySend(users)
            }
        awaitClose {
            listener.remove()
        }
    }

    suspend fun getUserById(uid:String):User?{
        return try {
            val snapshot= firestore.collection("users")
                .document(uid)
                .get().await()
            snapshot.toObject(User::class.java)
        }
        catch (e:Exception){
            null
        }
    }

    suspend fun updateProfileAccount(
        uid:String , newName :String , newBio:String
    ):Result<Unit>{
        return try {
            firestore
                .collection("users")
                .document(uid)
                .update(
                    mapOf(
                        "name" to newName ,
                        "bio" to newBio
                    )
                )
                .await()
            Result.success(Unit)
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}
