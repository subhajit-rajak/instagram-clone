package com.subhajitrajak.instagramclone.models

class Reel {
    var reelUrl: String= ""
    var caption: String= ""
    var userId: String?= null

    constructor()
    constructor(reelUrl: String, caption: String) {
        this.reelUrl = reelUrl
        this.caption = caption
    }

    constructor(reelUrl: String, caption: String, userId: String,) {
        this.reelUrl = reelUrl
        this.caption = caption
        this.userId = userId
    }
}