package com.example.social.model.daos

import com.example.social.model.Post
import com.example.social.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {
    private val db = FirebaseFirestore.getInstance()
     val postCollection = db.collection("Post")
    private val auth = Firebase.auth
     @OptIn(DelicateCoroutinesApi::class)
     fun addPost(text : String)
     {
         val currentUSerId = auth.currentUser!!.uid
         GlobalScope.launch {
             val userDao = UserDao()
             val user = userDao.getUserById(currentUSerId).await().toObject(User::class.java)!! // await will help the code to complete its task and then we call toObject to get our user as it was a task before
             val currTime = System.currentTimeMillis()
             val post = Post(text,user,currTime)
             postCollection.document().set(post)
         }
     }

    fun getPostById(postId : String): Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }
    fun updateLike(postId : String){

        GlobalScope.launch {
            val currentUSerId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!! // await will help the code to complete its task and then we call toObject to get our user as it was a task before
            val isLiked = post.likedBy.contains(currentUSerId)
            if(isLiked)
            {
                post.likedBy.remove(currentUSerId)
            }else{
                post.likedBy.add(currentUSerId)
            }
            postCollection.document(postId).set(post)
        }

    }

}