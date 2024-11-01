package com.subhajitrajak.instagramclone.models

class Post {
    var postUrl: String= ""
    var caption: String= ""
    var uid: String= ""
    var time: Long= 0
    var postId: String= ""
    var likes: MutableMap<String, Boolean> = HashMap()
    var comments: MutableList<Comment> = mutableListOf()

    constructor()
    constructor(postUrl: String, caption: String) {
        this.postUrl = postUrl
        this.caption = caption
    }

    constructor(postUrl: String, caption: String, name: String, time: Long, postId: String) {
        this.postUrl = postUrl
        this.caption = caption
        this.uid = name
        this.time = time
        this.postId = postId
    }

}