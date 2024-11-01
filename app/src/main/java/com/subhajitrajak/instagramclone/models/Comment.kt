package com.subhajitrajak.instagramclone.models

class Comment {
    var userId: String = ""
    var text: String = ""
    var time: Long = 0

    constructor()
    constructor(userId: String, text: String, time: Long) {
        this.userId = userId
        this.text = text
        this.time = time
    }
}