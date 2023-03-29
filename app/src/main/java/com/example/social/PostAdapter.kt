package com.example.social

import android.content.ClipData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.social.model.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listener : IPostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {
    var SECRET_KEY = "2043202ae3fb2420ase23fj21d83er82"
    var SECRET_IV = "2043202ae3fb2420"

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView.findViewById<ImageView>(R.id.userImage)
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val createdAt = itemView.findViewById<TextView>(R.id.createdAt)
        val postTitle = itemView.findViewById<TextView>(R.id.postTitle)
        val likeButton = itemView.findViewById<ImageView>(R.id.likeButton)
        val likeCount = itemView.findViewById<TextView>(R.id.likeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        )
        view.likeButton.setOnClickListener {
            listener.onLikedClicked(snapshots.getSnapshot(view.adapterPosition).id)
        }
        return view
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.userName.text = model.createdBy.displayName
        holder.postTitle.text = model.text?.decryptCBC()
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        holder.likeCount.text = model.likedBy.size.toString()
        Glide.with(holder.userImage.context).load(model.createdBy.photoUrl).circleCrop()
            .into(holder.userImage)
        val auth = Firebase.auth
        val currUser = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currUser)
        if (isLiked) {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context,
                    R.drawable.ic_liked
                )
            )
        } else {
            holder.likeButton.setImageDrawable(
                ContextCompat.getDrawable(
                    holder.likeButton.context,
                    R.drawable.ic_unliked
                )
            )
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
interface IPostAdapter{
    fun onLikedClicked(postId : String)
}