package com.application.shareman.remote.assistant

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.application.shareman.remote.model.Subject
import com.application.shareman.service.LocalStorage
import com.google.firebase.database.FirebaseDatabase

class UpdateSubjects(context: Context, subjects:ArrayList<Subject>) {

    val result= MutableLiveData<Boolean>()

    init {
        val db= FirebaseDatabase.getInstance().reference.child("users").child(LocalStorage(context).getUsername()).child("subjects")
        db.removeValue()
        for((c, i) in subjects.withIndex()){
            if(c == subjects.size - 1){
                db.child(c.toString()).setValue("${i.name},${i.type}").addOnCompleteListener {
                    if(it.isSuccessful){
                        LocalStorage(context).setMySubjects(subjects)
                        result.postValue(true)
                    }else result.postValue(false)
                }
            }else db.child(c.toString()).setValue("${i.name},${i.type}")
        }
    }

}