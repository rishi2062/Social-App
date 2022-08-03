package com.example.social

import android.content.ClipData
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

class PostAdapter(options: FirestoreRecyclerOptions<Post>, val listener : IPostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userImage = itemView.findViewById<ImageView>(R.id.userImage)
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val createdAt = itemView.findViewById<TextView>(R.id.createdAt)
        val postTitle = itemView.findViewById<TextView>(R.id.postTitle)
        val likeButton = itemView.findViewById<ImageView>(R.id.likeButton)
        val likeCount = itemView.findViewById<TextView>(R.id.likeCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view,parent,false))
        view.likeButton.setOnClickListener{
            listener.onLikedClicked(snapshots.getSnapshot(view.adapterPosition).id)
        }
        return view
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
        holder.userName.text = model.createdBy.displayName
        holder.postTitle.text = model.text
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)
        holder.likeCount.text = model.likedBy.size.toString()
        Glide.with(holder.userImage.context).load(model.createdBy.photoUrl).circleCrop().into(holder.userImage)
        val auth = Firebase.auth
        val currUser = auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currUser)
        if(isLiked) {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_liked))
        } else {
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context, R.drawable.ic_unliked))
        }
    }
}

interface IPostAdapter{
    fun onLikedClicked(postId : String)
}