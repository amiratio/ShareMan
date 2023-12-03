package com.application.shareman.service

import android.content.Context
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*

class Lottie(context: Context, show: Boolean) {

    init {
        ((Tools().unwrap(context)) as AppCompatActivity).lottie.setOnClickListener {  }
        if(show) ((Tools().unwrap(context)) as AppCompatActivity).lottie.visibility= View.VISIBLE
        else ((Tools().unwrap(context)) as AppCompatActivity).lottie.visibility= View.GONE
    }

}