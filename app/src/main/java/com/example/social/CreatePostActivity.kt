package com.example.social

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.social.databinding.ActivityCreatePostBinding
import com.example.social.model.Post
import com.example.social.model.daos.PostDao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class CreatePostActivity : AppCompatActivity() {
    var SECRET_KEY = "2043202ae3fb2420ase23fj21d83er82"
    var SECRET_IV = "2043202ae3fb2420"
    private lateinit var binding: ActivityCreatePostBinding
    private lateinit var postDao: PostDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postDao = PostDao()
        binding.post.setOnClickListener {
            val input = binding.update.text.toString().encryptCBC()
            if (input.isNotEmpty()) {
                postDao.addPost(input)
                Log.d("Add hua", "Added")
                try {
                    finish() // This is we using for that when user submit its post he will automatially come to mainActivity
                } catch (e: Exception) {
                    Log.e("Error in finish ", e.toString())
                }

            }

        }
        binding.LogOut.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this@CreatePostActivity, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.tool.setOnClickListener {
            val intent = Intent(this, EncryptDecrypt::class.java)
            startActivity(intent)
        }
    }


    @OptIn(ExperimentalEncodingApi::class)
    private fun String.encryptCBC(): String {
        try {
            val iv = IvParameterSpec(SECRET_IV.toByteArray())
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
            val crypted = cipher.doFinal(this.toByteArray())
            val encodedByte = Base64.encode(crypted)
            // val encodedByte = android.util.Base64.encode(crypted, android.util.Base64.DEFAULT)
            return encodedByte
        } catch (e: Exception) {
            Log.e("Not Encoded Error : ", e.toString())
            return ""
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun String.decryptCBC(): String {
        try {
//            val decodedByte : ByteArray =  android.util.Base64.decode(this,android.util.Base64.DEFAULT)
            val decodedByte: ByteArray = Base64.decode(this)
            val iv = IvParameterSpec(SECRET_IV.toByteArray())
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
            val decrypted = cipher.doFinal(decodedByte)
            return String(decrypted)
        } catch (e: Exception) {
            Log.e("Not Decoded Error : ", e.toString())
            return ""
        }
    }
}