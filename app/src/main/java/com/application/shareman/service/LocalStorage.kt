package com.application.shareman.service

import android.content.Context
import com.application.shareman.remote.model.Subject

class LocalStorage(private val context: Context) {

    //---------------------------------------------------------------------------- user
    fun getUsername(): String{ return context.getSharedPreferences("user", 0).getString("username", "")!! }
    fun getName(): String{ return context.getSharedPreferences("user", 0).getString("name", "")!! }
    fun getEmail(): String{ return context.getSharedPreferences("user", 0).getString("email", "")!! }
    fun getFcmToken(): String{ return context.getSharedPreferences("user", 0).getString("fcmToken", "")!! }
    fun getAvatar(): String{ return context.getSharedPreferences("user", 0).getString("avatar", "")!! }
    fun getBio(): String{ return context.getSharedPreferences("user", 0).getString("bio", "")!! }
    fun getJob(): String{ return context.getSharedPreferences("user", 0).getString("job", "")!! }
    fun getUserType(): String{ return context.getSharedPreferences("user", 0).getString("userType", "0")!! }
    fun getMySubjects(): ArrayList<Subject>{
        val list= ArrayList<Subject>()
        for(i in context.getSharedPreferences("subjects", 0).all) list.add(Subject(i.key.toString(), i.value.toString()))
        return list
    }
    //---------------------------------------------------------------------------- settings
    fun getMaxSubjectSelection(): String{ return context.getSharedPreferences("settings", 0).getString("maxSubjectSelection", "")!! }
    fun getMaxPostVideoDuration(): String{ return context.getSharedPreferences("settings", 0).getString("maxPostVideoDuration", "")!! }
    fun getSubjectsList(): ArrayList<Subject>{
        val list= ArrayList<Subject>()
        for(i in context.getSharedPreferences("subjectsList", 0).all) if(getUserType() == i.value.toString()) list.add(Subject(i.key.toString(), i.value.toString()))
        return list
    }


    //**********************************************************************************************


    //---------------------------------------------------------------------------- user
    fun setUsername(username: String){ context.getSharedPreferences("user", 0).edit().putString("username", username).apply() }
    fun setName(name: String){ context.getSharedPreferences("user", 0).edit().putString("name", name).apply() }
    fun setEmail(email: String){ context.getSharedPreferences("user", 0).edit().putString("email", email).apply() }
    fun setFcmToken(fcmToken: String){ context.getSharedPreferences("user", 0).edit().putString("fcmToken", fcmToken).apply() }
    fun setAvatar(avatar: String){ context.getSharedPreferences("user", 0).edit().putString("avatar", avatar).apply() }
    fun setBio(bio: String){ context.getSharedPreferences("user", 0).edit().putString("bio", bio).apply() }
    fun setJob(job: String){ context.getSharedPreferences("user", 0).edit().putString("job", job).apply() }
    fun setUserType(userType: String){ context.getSharedPreferences("user", 0).edit().putString("userType", userType).apply() }
    fun setMySubjects(subjects: ArrayList<Subject>){
        context.getSharedPreferences("subjects", 0).edit().clear().apply()
        for(i in subjects) context.getSharedPreferences("subjects", 0).edit().putString(i.name, i.type).apply()
    }
    //---------------------------------------------------------------------------- settings
    fun setMaxSubjectSelection(maxSubjectSelection: String){ context.getSharedPreferences("settings", 0).edit().putString("maxSubjectSelection", maxSubjectSelection).apply() }
    fun setMaxPostVideoDuration(maxPostVideoDuration: String){ context.getSharedPreferences("settings", 0).edit().putString("maxPostVideoDuration", maxPostVideoDuration).apply() }
    fun setSubjectsList(subjects: ArrayList<Subject>){
        context.getSharedPreferences("subjectsList", 0).edit().clear().apply()
        for(i in subjects) context.getSharedPreferences("subjectsList", 0).edit().putString(i.name, i.type).apply()
    }

}