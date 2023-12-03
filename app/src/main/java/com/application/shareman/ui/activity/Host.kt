package com.application.shareman.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.application.shareman.R
import com.application.shareman.remote.assistant.PublishPost
import com.application.shareman.service.HostNotificationHandler
import com.application.shareman.service.Tools
import com.application.shareman.ui.fragment.AddNewPost
import com.application.shareman.ui.fragment.Home
import com.application.shareman.ui.fragment.Profile
import kotlinx.android.synthetic.main.activity_host.*

class Host : AppCompatActivity() {

    private var currentFragment= ""

    override fun onBackPressed() {
        when(currentFragment){
            "addNewPost" -> profile()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host)
        Tools().backgroundStatusBar(this, window)

        navigation()
        home()
    }

    fun home(){
        if(currentFragment != "home"){
            clearNavigation()
            home.imageTintList= ContextCompat.getColorStateList(this, R.color.yellow)
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, Home()).addToBackStack(null).commit()
            currentFragment= "home"
            frameLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        }
    }

    fun profile(){
        if(currentFragment != "profile"){
            clearNavigation()
            profile.imageTintList= ContextCompat.getColorStateList(this, R.color.yellow)
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, Profile()).addToBackStack(null).commit()
            currentFragment= "profile"
            frameLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        }
    }

    fun addNewPost(){
        if(currentFragment != "addNewPost"){
            clearNavigation()
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout, AddNewPost()).addToBackStack(null).commit()
            currentFragment= "addNewPost"
            frameLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        }
    }

    fun publishPost(){
        PublishPost(this)
    }

    private fun navigation(){
        profile.setOnClickListener { profile() }
        home.setOnClickListener { home() }
    }

    private fun clearNavigation(){
        profile.imageTintList= ContextCompat.getColorStateList(this, R.color.white)
        home.imageTintList= ContextCompat.getColorStateList(this, R.color.white)
        find.imageTintList= ContextCompat.getColorStateList(this, R.color.white)
    }

    fun back(view: View)= onBackPressed()
}