package com.application.shareman.remote.assistant

import android.content.Context
import com.application.shareman.remote.model.Subject
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.SingleLiveEvent
import com.google.firebase.database.FirebaseDatabase

class GetUser(context: Context) {

    val result: SingleLiveEvent<Boolean> by lazy { SingleLiveEvent<Boolean>() }

    init {
        FirebaseDatabase.getInstance().reference.child("users").child(LocalStorage(context).getUsername()).get().addOnSuccessListener {
            if(it.hasChild("name")) LocalStorage(context).setName(it.child("name").value.toString())
            if(it.hasChild("username")) LocalStorage(context).setUsername(it.child("username").value.toString())
            if(it.hasChild("avatar")) LocalStorage(context).setAvatar(it.child("avatar").value.toString())
            if(it.hasChild("email")) LocalStorage(context).setEmail(it.child("email").value.toString())
            if(it.hasChild("bio")) LocalStorage(context).setBio(it.child("bio").value.toString())
            if(it.hasChild("job")) LocalStorage(context).setJob(it.child("job").value.toString())
            result.postValue(true)
        }
    }

}