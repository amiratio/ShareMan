package com.application.shareman.service

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class PicassoPicture(context: Context, imageView: ImageView, url: String) {

    init {
        Picasso.with(context).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, object : Callback {
            override fun onSuccess() {}
            override fun onError() { Picasso.with(context).load(url).into(imageView) }
        })
    }

}