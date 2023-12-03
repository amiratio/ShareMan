package com.application.shareman.remote.assistant

import android.content.Context
import com.application.shareman.remote.model.Subject
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.SingleLiveEvent
import com.google.firebase.database.FirebaseDatabase

class GetSettings(context: Context) {

    val result: SingleLiveEvent<Boolean> by lazy { SingleLiveEvent<Boolean>() }

    init {
        FirebaseDatabase.getInstance().reference.child("settings").get().addOnSuccessListener {
            if(it.hasChild("maxSubjectSelection")) LocalStorage(context).setMaxSubjectSelection(it.child("maxSubjectSelection").value.toString())
            if(it.hasChild("maxPostVideoDuration")) LocalStorage(context).setMaxPostVideoDuration(it.child("maxPostVideoDuration").value.toString())
            if(it.hasChild("subjects")){
                val list= ArrayList<Subject>()
                for(i in it.child("subjects").children) list.add(Subject(i.value.toString().split(",")[0], i.value.toString().split(",")[1]))
                LocalStorage(context).setSubjectsList(list)
            }
            result.postValue(true)
        }
    }

}