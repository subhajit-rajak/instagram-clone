package com.subhajitrajak.instagramclone.Models

class Post {
    var postUrl: String= ""
    var caption: String= ""
    var uid: String= ""
    var time: Long= 0
    constructor()
    constructor(postUrl: String, caption: String) {
        this.postUrl = postUrl
        this.caption = caption
    }

    constructor(postUrl: String, caption: String, name: String, time: Long) {
        this.postUrl = postUrl
        this.caption = caption
        this.uid = name
        this.time = time
    }

}