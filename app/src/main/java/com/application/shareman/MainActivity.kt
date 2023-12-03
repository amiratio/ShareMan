package com.application.shareman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.application.shareman.remote.assistant.GetSettings
import com.application.shareman.remote.assistant.GetUser
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.Tools
import com.application.shareman.ui.activity.Host
import com.application.shareman.ui.auth.Register
import com.application.shareman.ui.auth.Subjects
import com.google.firebase.auth.FirebaseAuth
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Tools().backgroundStatusBar(this, window)

        fixedRateTimer("splashScreen", false, 500, 1000){
            runOnUiThread {
                if(FirebaseAuth.getInstance().uid != null){
                    if(LocalStorage(this@MainActivity).getMySubjects().isNotEmpty()){
                        GetUser(this@MainActivity).result.observe(this@MainActivity){
                            val intent= Intent(this@MainActivity, Host::class.java)
                            startActivity(intent)
                            finish()
                        }
                        GetSettings(this@MainActivity)
                    }else{
                        val intent= Intent(this@MainActivity, Subjects::class.java)
                        startActivity(intent)
                        finish()
                    }
                }else{
                    val intent= Intent(this@MainActivity, Register::class.java)
                    startActivity(intent)
                    finish()
                }
                this.cancel()
            }
        }
    }
}