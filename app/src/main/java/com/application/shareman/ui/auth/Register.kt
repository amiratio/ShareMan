package com.application.shareman.ui.auth

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.application.shareman.R
import com.application.shareman.remote.assistant.UpdateAvatar
import com.application.shareman.remote.assistant.UsernameValidation
import com.application.shareman.service.Constants
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.Lottie
import com.application.shareman.service.Tools
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.permissionx.guolindev.PermissionX
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File

class Register : AppCompatActivity() {

    private var usernameValidation: UsernameValidation?= null
    private var avatarUri: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        Tools().backgroundStatusBar(this, window)
    }

    fun next(view: View){
        if(fullName.text.toString().isNotBlank() && username.text.toString().isNotBlank() && Tools().emailMatcher(email.text.toString()) && password.text.toString().isNotBlank()){
            if(password.text.toString().length >= 6){
                if(usernameValidation == null){
                    Lottie(this, true)
                    usernameValidation= UsernameValidation(username.text.toString())
                    usernameValidation!!.result.observe(this){
                        Lottie(this, false)
                        if(it) createUser()
                        else{
                            Tools().alertDialog(this, getString(R.string.invalidUsername))
                        }
                    }
                }else usernameValidation!!.check(username.text.toString())
            }else Tools().alertDialog(this, getString(R.string.invalidPassword))
        }else Tools().alertDialog(this, getString(R.string.fillFields))
    }

    private fun createUser(){
        Lottie(this, true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).addOnCompleteListener {
            if(it.isSuccessful) createUserDatabase()
            else Tools().alertDialog(this, getString(R.string.invalidEmail))
        }
    }

    private fun createUserDatabase(){
        val uid= FirebaseAuth.getInstance().currentUser?.uid!!
        Log.d("user", "UID -> $uid")
        val db= FirebaseDatabase.getInstance().reference.child("users").child(username.text.toString())
        db.child("name").setValue(fullName.text.toString())
        db.child("username").setValue(username.text.toString())
        db.child("email").setValue(email.text.toString())
        db.child("uid").setValue(uid)
        LocalStorage(this).setName(fullName.text.toString())
        LocalStorage(this).setEmail(email.text.toString())
        LocalStorage(this).setUsername(username.text.toString())
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isSuccessful){
                db.child("fcm").setValue(it.result.toString())
                LocalStorage(this).setFcmToken(it.result.toString())
                Log.d("user", "FCM TOKEN -> ${it.result}")
            }
        }
        if(avatarUri != null){
            UpdateAvatar(this, avatarUri!!).result.observe(this){
                gotoSubjects()
            }
        }else gotoSubjects()
    }

    private fun gotoSubjects(){
        Lottie(this, false)
        val intent= Intent(this, Subjects::class.java)
        intent.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    fun selectPicture(view: View){
        PermissionX.init(this).permissions(Manifest.permission.READ_EXTERNAL_STORAGE).request { allGranted, _, _ ->
            if (allGranted) {
                EasyImage.openGallery(this, 101)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, object : DefaultCallback() {
                override fun onImagePickerError(e: java.lang.Exception, source: EasyImage.ImageSource, type: Int) {}
                override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                    val tools= Tools()
                    tools.getCompressedImage(this@Register, Constants.avatarQuality, imageFiles[0].path)
                    tools.getCompressedImageUriResult.observe(this@Register){
                        Glide.with(this@Register).load(it).into(avatar)
                        avatarUri= it
                    }
                }
                override fun onCanceled(source: EasyImage.ImageSource, type: Int) {}
            })

        }catch (e: Exception){}
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
            window.decorView.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}