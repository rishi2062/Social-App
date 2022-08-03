package com.example.social.model

class Post(
    var text : String? = null,
    var createdBy : User  = User(),
    var createdAt : Long = 0L,
    var likedBy : ArrayList<String> = ArrayList())