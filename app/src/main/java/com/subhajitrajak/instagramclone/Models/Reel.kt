package com.subhajitrajak.instagramclone.Models

class Reel {
    var reelUrl: String= ""
    var caption: String= ""
    var profileLink: String?= null
    var profileName: String= ""

    constructor()
    constructor(reelUrl: String, caption: String) {
        this.reelUrl = reelUrl
        this.caption = caption
    }

    constructor(reelUrl: String, caption: String, profileLink: String, profileName: String) {
        this.reelUrl = reelUrl
        this.caption = caption
        this.profileLink = profileLink
        this.profileName = profileName
    }
}