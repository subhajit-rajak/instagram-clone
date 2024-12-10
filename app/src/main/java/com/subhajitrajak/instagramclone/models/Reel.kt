package com.subhajitrajak.instagramclone.models

class Reel {
    var reelUrl: String= ""
    var caption: String= ""
    var userId: String= ""

    var time: Long= 0
    var reelId: String= ""
    var likes: MutableMap<String, Boolean> = HashMap()
    var comments: MutableList<Comment> = mutableListOf()

    constructor()
    constructor(reelUrl: String, caption: String) {
        this.reelUrl = reelUrl
        this.caption = caption
    }

    constructor(reelUrl: String, caption: String, userId: String, time: Long, reelId: String) {
        this.reelUrl = reelUrl
        this.caption = caption
        this.userId = userId
        this.time = time
        this.reelId = reelId
    }
}