package com.application.shareman.remote.assistant

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.application.shareman.R
import com.application.shareman.service.*
import com.application.shareman.service.cache.PostPublishCache
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_host.*
import java.io.File
import kotlin.concurrent.fixedRateTimer

class PublishPost(private val context: Context) {

    private val notification= HostNotificationHandler(context, (context as AppCompatActivity).notification)

    init {
        val timeStamp= Tools().createTimeStamp()
        when(PostPublishCache.type){
            "picture" -> {
                if(PostPublishCache.uri != null) uploadPicture(timeStamp)
                else notification.postSendingFailed()
            }
            "video" -> {
                if(PostPublishCache.uri != null) uploadVideo(timeStamp)
                else notification.postSendingFailed()
            }
            "text" -> {
                notification.postSending()
                uploadData(timeStamp, "")
            }
        }
    }

    private fun uploadVideo(timeStamp: String){
        notification.postSending()
        var c= 0
        val timer= fixedRateTimer("upload", false, 1000, 500){
            (context as AppCompatActivity).runOnUiThread {
                notification.textUpdate("${context.getString(R.string.uploadingPost)}  ($c%)")
                if(c < 50) c++
                else this.cancel()
            }
        }
        VideoCompressor(context, PostPublishCache.uri!!).result.observe(context as LifecycleOwner){ compressedVide->
            val uid= FirebaseAuth.getInstance().currentUser?.uid!!
            val storage= FirebaseStorage.getInstance().reference
            val uploadPath= storage.child("subjects").child(PostPublishCache.subject).child(uid).child(timeStamp)
            uploadPath.putFile(compressedVide).addOnSuccessListener {
                uploadPath.downloadUrl.addOnSuccessListener {
                    uploadData(timeStamp, it.toString())
                }.addOnFailureListener { notification.postSendingFailed() }
            }.addOnFailureListener { notification.postSendingFailed() }
                .addOnProgressListener {
                    timer.cancel()
                    c= 50 + ((100.00*it.bytesTransferred / it.totalByteCount).toInt() / 2)
                    notification.textUpdate("${context.getString(R.string.uploadingPost)}  ($c%)")
                    //uploadProgress.value= (100.00*it.bytesTransferred / it.totalByteCount).toInt()
                }
        }
    }

    private fun uploadPicture(timeStamp: String){
        notification.postSending()
        val uid= FirebaseAuth.getInstance().currentUser?.uid!!
        val storage= FirebaseStorage.getInstance().reference
        val uploadPath= storage.child("subjects").child(PostPublishCache.subject).child(uid).child(timeStamp)
        uploadPath.putFile(PostPublishCache.uri!!).addOnSuccessListener {
            uploadPath.downloadUrl.addOnSuccessListener {
                uploadData(timeStamp, it.toString())
            }.addOnFailureListener { notification.postSendingFailed() }
        }.addOnFailureListener { notification.postSendingFailed() }
            .addOnProgressListener {
                //uploadProgress.value= (100.00*it.bytesTransferred / it.totalByteCount).toInt()
            }
    }

    private fun uploadData(timeStamp: String, mediaUrl: String){
        with(PostPublishCache){
            val userDB= FirebaseDatabase.getInstance().reference.child("users").child(LocalStorage(context).getUsername()).child("posts").child(timeStamp)
            val subjectDB= FirebaseDatabase.getInstance().reference.child("subjects").child(subject).child(timeStamp)

            if(mediaUrl.isNotBlank()) subjectDB.child("media").setValue(mediaUrl)
            if(content.isNotBlank()) subjectDB.child("content").setValue(content)
            if(location != null) subjectDB.child("geo").setValue("${location?.latitude.toString()},${location?.longitude.toString()}")
            subjectDB.child("type").setValue(type)
            subjectDB.child("comments").setValue(comments)
            subjectDB.child("name").setValue(LocalStorage(context).getName())
            subjectDB.child("username").setValue(LocalStorage(context).getUsername())

            if(mediaUrl.isNotBlank()) userDB.child("media").setValue(mediaUrl)
            if(content.isNotBlank()) userDB.child("content").setValue(content)
            if(location != null) userDB.child("geo").setValue("${location?.latitude.toString()},${location?.longitude.toString()}")
            userDB.child("path").setValue("subjects/${subject}/${timeStamp}")
            userDB.child("type").setValue(type)
            userDB.child("comments").setValue(comments)
            subjectDB.child("name").setValue(LocalStorage(context).getName())
            subjectDB.child("username").setValue(LocalStorage(context).getUsername()).addOnCompleteListener {
                if(it.isSuccessful) notification.postSent()
            }
        }
    }

}