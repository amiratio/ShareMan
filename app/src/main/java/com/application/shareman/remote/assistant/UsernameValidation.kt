package com.application.shareman.remote.assistant

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class UsernameValidation(username: String) {

    val result= MutableLiveData<Boolean>()
    private lateinit var dataSnapshot: DataSnapshot

    init {
        FirebaseDatabase.getInstance().reference.child("usernames").get().addOnSuccessListener {
            dataSnapshot= it
            check(username)
        }
    }

    fun check(username: String){
        result.value = !dataSnapshot.hasChild(username)
    }

}