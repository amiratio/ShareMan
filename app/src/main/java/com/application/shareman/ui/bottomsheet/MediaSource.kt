package com.application.shareman.ui.bottomsheet

import android.Manifest
import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.shareman.R
import com.application.shareman.service.SingleLiveEvent
import com.application.shareman.service.Tools
import com.application.shareman.ui.bottomsheet.adapter.DropDownAdapter
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.bottomsheet_dropdown.*
import kotlinx.android.synthetic.main.bottomsheet_media_source.*
import pl.aprilapps.easyphotopicker.EasyImage

class MediaSource(private val context: Context, private val type: String) {

    val result: SingleLiveEvent<String> by lazy { SingleLiveEvent<String>() }

    init {
        val dialog= BottomSheetDialog(context, R.style.TransparentBottomSheet)
        dialog.setContentView(R.layout.bottomsheet_media_source)
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED

        with(dialog){
            if(type == "picture"){
                option2.setOnClickListener {
                    PermissionX.init(Tools().unwrap(context) as AppCompatActivity).permissions(Manifest.permission.READ_EXTERNAL_STORAGE).request { allGranted, _, _ ->
                        if(allGranted){
                            result.postValue("pictureGallery")
                            dismiss()
                        }
                    }
                }
                option1.setOnClickListener {
                    PermissionX.init(Tools().unwrap(context) as AppCompatActivity).permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).request { allGranted, _, _ ->
                        if(allGranted){
                            result.postValue("pictureCamera")
                            dismiss()
                        }
                    }
                }
            }else if(type == "video"){
                option1_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.im_video_camera))
                option2_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.im_video_gallery))
                option2.setOnClickListener {
                    PermissionX.init(Tools().unwrap(context) as AppCompatActivity).permissions(Manifest.permission.READ_EXTERNAL_STORAGE).request { allGranted, _, _ ->
                        if(allGranted){
                            result.postValue("videoGallery")
                            dismiss()
                        }
                    }
                }
                option1.setOnClickListener {
                    PermissionX.init(Tools().unwrap(context) as AppCompatActivity).permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE).request { allGranted, _, _ ->
                        if(allGranted){
                            result.postValue("videoCamera")
                            dismiss()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

}