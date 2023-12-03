package com.application.shareman.service

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class DatabaseSingleRead(path: String) {

    val result: SingleLiveEvent<DataSnapshot> by lazy { SingleLiveEvent<DataSnapshot>() }

    init {
        FirebaseDatabase.getInstance().reference.child(path).get().addOnSuccessListener {
            result.postValue(it)
        }
    }

}