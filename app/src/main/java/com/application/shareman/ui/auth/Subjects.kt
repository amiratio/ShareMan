package com.application.shareman.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.application.shareman.R
import com.application.shareman.remote.assistant.GetSettings
import com.application.shareman.remote.assistant.UpdateSubjects
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.Lottie
import com.application.shareman.service.Tools
import com.application.shareman.ui.activity.Host
import com.application.shareman.ui.adapter.SubjectsAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.android.synthetic.main.activity_subjects.*

class Subjects : AppCompatActivity() {

    private lateinit var adapter: SubjectsAdapter

    override fun onBackPressed() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subjects)
        Tools().backgroundStatusBar(this, window)

        setup()
    }

    private fun setup(){
        Lottie(this, true)
        GetSettings(this).result.observe(this){
            recyclerView.layoutManager= FlexboxLayoutManager(this)
            adapter= SubjectsAdapter(LocalStorage(this).getSubjectsList())
            recyclerView.adapter= adapter
            Lottie(this, false)
        }
    }

    fun start(view: View){
        if(adapter.selected.isNotEmpty()){
            Lottie(this, true)
            UpdateSubjects(this, adapter.selected).result.observe(this){
                if(it){
                    val intent= Intent(this, Host::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
    }
}