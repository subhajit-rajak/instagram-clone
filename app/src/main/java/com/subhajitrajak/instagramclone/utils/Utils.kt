@file:Suppress("DEPRECATION")

package com.subhajitrajak.instagramclone.utils

import android.app.ProgressDialog
import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.subhajitrajak.instagramclone.models.User
import java.util.UUID

fun uploadImage(uri: Uri, folderName: String, callback:(String?)-> Unit) {
    var imageUrl:String?
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUrl=it.toString()
                callback(imageUrl)
            }
        }
}

fun uploadVideo(uri: Uri, folderName: String, progressDialog: ProgressDialog, callback:(String?)-> Unit) {
    var imageUrl:String?
    progressDialog.setTitle("Uploading...")
    progressDialog.show()

    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUrl=it.toString()
                progressDialog.dismiss()
                callback(imageUrl)
            }
        }
        .addOnProgressListener {
            val uploadedValue: Long = (it.bytesTransferred/it.totalByteCount)*100
            progressDialog.setMessage("Uploaded $uploadedValue%")
        }
}

fun usernameAlreadyExists(username: String, callback: (Boolean) -> Unit) {
    Firebase.firestore.collection(USER_NODE).get()
        .addOnSuccessListener { result ->
            val set = HashSet<String>()
            for (userObj in result.documents) {
                val user: User = userObj.toObject<User>()!!
                user.username?.let { set.add(it) }
            }
            callback(set.contains(username))
        }
        .addOnFailureListener {
            callback(false)
        }
}

fun isUsernameValid(username: String): Boolean {
    val regex = "^[a-zA-Z0-9._]+$".toRegex()
    return regex.matches(username)
}