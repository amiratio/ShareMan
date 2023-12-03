package com.application.shareman.remote.assistant

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.SingleLiveEvent
import com.application.shareman.service.Tools
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class UpdateAvatar(context: Context, uri: Uri) {

    val result: SingleLiveEvent<Boolean> by lazy { SingleLiveEvent<Boolean>() }

    init {
        val uid= FirebaseAuth.getInstance().currentUser?.uid!!
        val storage= FirebaseStorage.getInstance().reference
        val uploadPath= storage.child("users").child(uid).child("avatar")
        uploadPath.putFile(uri).addOnSuccessListener {
            uploadPath.downloadUrl.addOnSuccessListener {
                FirebaseDatabase.getInstance().reference.child("users").child(LocalStorage(context).getUsername()).child("avatar").setValue(it.toString())
                LocalStorage(context).setAvatar(it.toString())
                result.postValue(true)
            }.addOnFailureListener { result.postValue(false) }
        }.addOnFailureListener { result.postValue(false) }
            .addOnProgressListener {
                //uploadProgress.value= (100.00*it.bytesTransferred / it.totalByteCount).toInt()
            }
    }

}