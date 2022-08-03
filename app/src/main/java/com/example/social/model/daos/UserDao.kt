package com.example.social.model.daos

import com.example.social.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("users")
    @OptIn(DelicateCoroutinesApi::class)
    fun addUser(user : User?){
        user?.let{
            GlobalScope.launch {
                userCollection.document(user.id).set(it)
            }

        }
    }
    fun getUserById(uId : String): Task<DocumentSnapshot> {
        return userCollection.document(uId).get()
    }

}