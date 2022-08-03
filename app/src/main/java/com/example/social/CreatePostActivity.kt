package com.example.social

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.social.databinding.ActivityCreatePostBinding
import com.example.social.model.Post
import com.example.social.model.daos.PostDao

class CreatePostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreatePostBinding
    private lateinit var postDao : PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postDao = PostDao()
        binding.post.setOnClickListener{
            val input = binding.update.text.toString().trim()
            if(input.isNotEmpty())
            {
                postDao.addPost(input)
                finish() // This is we using for that when user submit its post he will automatially come to mainActivity
            }
        }
    }
}