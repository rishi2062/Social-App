package com.example.social

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.social.databinding.ActivityMainBinding
import com.example.social.model.Post
import com.example.social.model.daos.PostDao
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import retrofit2.http.POST

class MainActivity : AppCompatActivity(), IPostAdapter {
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding : ActivityMainBinding
    private lateinit var postDao : PostDao
    lateinit var adapter : PostAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.add.setOnClickListener{
            val postIntent = Intent(this,CreatePostActivity::class.java)
            startActivity(postIntent)
        }
        val postDao = PostDao()
        val postsCollections = postDao.postCollection
        val query = postsCollections.orderBy("createdAt", Query.Direction.DESCENDING)
        val recyclerViewOptions = FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post::class.java).build()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(recyclerViewOptions,this)
        binding.recyclerView.adapter = adapter

    }

    override fun onStart() {
        super.onStart()

        adapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onLikedClicked(postId: String) {
        postDao = PostDao()
        postDao.updateLike(postId)
    }
}