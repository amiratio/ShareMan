package com.application.shareman.service

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.application.shareman.R
import kotlinx.android.synthetic.main.view_notification.view.*
import kotlin.concurrent.fixedRateTimer

class HostNotificationHandler(private val context: Context, private val notificationPlaceHolder: View) {

    private fun clear(){
        with(notificationPlaceHolder){
            notification_lottieUploading.visibility= View.GONE
        }
    }

    fun postSending(){
        clear()
        with(notificationPlaceHolder){
            this.visibility= View.VISIBLE
            notification_background.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
            notification_background.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down))
            notification_text.text= context.getString(R.string.uploadingPost)
            notification_text.setTextColor(ContextCompat.getColor(context, R.color.black))
            notification_lottieUploading.visibility= View.VISIBLE
        }
    }

    fun postSent(){
        clear()
        with(notificationPlaceHolder){
            this@with.visibility= View.VISIBLE
            notification_text.text= context.getString(R.string.postUploaded)
            notification_text.setTextColor(ContextCompat.getColor(context, R.color.white))
            notification_background.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            notification_background.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down))
            var c= 0
            fixedRateTimer("host", false, 1500, 300){
                (context as AppCompatActivity).runOnUiThread {
                    if(c == 0){
                        notification_background.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
                        c++
                    }else{
                        this@with.visibility= View.GONE
                        this.cancel()
                    }
                }
            }
        }
    }

    fun postSendingFailed(){
        clear()
        with(notificationPlaceHolder){
            this@with.visibility= View.VISIBLE
            notification_text.text= context.getString(R.string.postUploadingFailed)
            notification_text.setTextColor(ContextCompat.getColor(context, R.color.white))
            notification_background.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            notification_background.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down))
            var c= 0
            fixedRateTimer("host", false, 1500, 300){
                (context as AppCompatActivity).runOnUiThread {
                    if(c == 0){
                        notification_background.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up))
                        c++
                    }else{
                        this@with.visibility= View.GONE
                        this.cancel()
                    }
                }
            }
        }
    }

    fun textUpdate(text: String){
        with(notificationPlaceHolder){
            notification_text.text= text
        }
    }

}