package com.application.shareman.ui.fragment

import android.Manifest
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.application.shareman.R
import com.application.shareman.remote.model.DropDown
import com.application.shareman.service.Constants
import com.application.shareman.service.LocalStorage
import com.application.shareman.service.cache.PostPublishCache
import com.application.shareman.service.Tools
import com.application.shareman.ui.activity.Host
import com.application.shareman.ui.bottomsheet.MediaSource
import com.application.shareman.ui.dialog.SelectLocation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.model.LatLng
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.fragment_add_new_post.*
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.File


class AddNewPost : Fragment() {

    private var lat= ""
    private var lon= ""
    private var subjects= ArrayList<DropDown>()
    private var mediaUri: Uri?= null
    private var type= "picture"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_new_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        buttons()
    }

    private fun setup(){
        with(PostPublishCache){
            uri= null
            content= ""
            type= ""
            subject= ""
            location= null
            comments= true
        }
        for(i in LocalStorage(requireContext()).getMySubjects()) subjects.add(DropDown(i.name, i.type, false))
    }

    private fun buttons(){
        fun clear(){
            thumbnail.setImageDrawable(null)
            importMedia.visibility= View.VISIBLE
            mediaUri= null
            picture.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            video.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            text.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            mediaFrame.visibility= View.GONE
            content.minHeight= 0
        }
        picture.setOnClickListener {
            clear()
            picture.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            importMedia.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.im_picture))
            mediaFrame.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
            mediaFrame.visibility= View.VISIBLE
            type= "picture"
        }
        video.setOnClickListener {
            clear()
            video.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            importMedia.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.im_video))
            mediaFrame.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
            mediaFrame.visibility= View.VISIBLE
            type= "video"
        }
        text.setOnClickListener {
            clear()
            text.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow))
            content.minHeight= 500
            type= "text"
        }

        locationLayout.setOnClickListener {
            PermissionX.init(this).permissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION).request { allGranted, _, _ ->
                if (allGranted) {
                    val dialog= SelectLocation(lat, lon)
                    dialog.show(requireActivity().supportFragmentManager, "SelectLocation")
                    dialog.result.observe(viewLifecycleOwner){
                        lat= it.latitude.toString()
                        lon= it.longitude.toString()
                        locationIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.im_done))
                    }
                }
            }
        }
        subjectLayout.setOnClickListener {
            com.application.shareman.ui.bottomsheet.DropDown(requireContext(), subjects, isFlexBoxStyle = true).result.observe(viewLifecycleOwner){
                subjects= it
                for(i in it) if(i.selected) subject.text= i.name
                subjectIcon.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.im_done))
            }
        }
        importMedia.setOnClickListener {
            importMedia()
        }
        thumbnail.setOnClickListener {
            importMedia()
        }

        publish.setOnClickListener {
            with(PostPublishCache){
                var dataReady= true

                uri= mediaUri
                content= this@AddNewPost.content.text.toString()
                type= this@AddNewPost.type
                if(type != "text"){
                    if(mediaUri == null){
                        dataReady= false
                        Tools().alertDialog(requireContext(), getString(R.string.selectMediaError))
                    }
                }
                if(lat.isNotBlank() && lon.isNotBlank()) location= LatLng(lat.toDouble(), lon.toDouble())
                if(closeComments.isChecked) comments= false

                var isOrNot= false
                for(i in subjects){
                    if(i.selected){
                        subject= i.name
                        isOrNot= true
                        break
                    }
                }
                if(!isOrNot){
                    dataReady= false
                    Tools().alertDialog(requireContext(), getString(R.string.selectCategoryError))
                }

                if(dataReady){
                    (activity as Host).publishPost()
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private fun importMedia(){
        MediaSource(requireActivity(), type).result.observe(viewLifecycleOwner){
            when(it){
                "pictureGallery" -> EasyImage.openGallery(this, 101)
                "pictureCamera" -> EasyImage.openCameraForImage(this, 102)
                "videoGallery" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "video/*"
                    startActivityForResult(Intent.createChooser(intent, ""), 2)
                }
                "videoCamera" -> {
                    type= "video"
                    val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, LocalStorage(requireContext()).getMaxPostVideoDuration())
                    takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStorageDirectory().path + "video.mp4")
                    startActivityForResult(takeVideoIntent, 3)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if(type == "picture"){
                EasyImage.handleActivityResult(requestCode, resultCode, data, requireActivity(), object : DefaultCallback() {
                    override fun onImagePickerError(e: java.lang.Exception, source: EasyImage.ImageSource, type: Int) {}
                    override fun onImagesPicked(imageFiles: List<File>, source: EasyImage.ImageSource, type: Int) {
                        val tools= Tools()
                        tools.getCompressedImage(requireContext(), Constants.postPictureQuality, imageFiles[0].path)
                        tools.getCompressedImageUriResult.observe(viewLifecycleOwner){
                            mediaUri= it
                            Glide.with(requireActivity()).load(mediaUri).into(thumbnail)
                            importMedia.visibility= View.GONE
                        }
                    }
                    override fun onCanceled(source: EasyImage.ImageSource, type: Int) {}
                })
            }else if(type == "video"){
                val retriever= MediaMetadataRetriever()
                retriever.setDataSource(context, data?.data)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMilliseconds = time?.toLong()
                retriever.release()

                if((timeInMilliseconds?:0L) <= (LocalStorage(requireContext()).getMaxPostVideoDuration().toLong() * 1000)){
                    mediaUri= data?.data
                    Glide.with(requireActivity()).load(mediaUri).apply(RequestOptions().frame(0)).into(thumbnail)
                    importMedia.visibility= View.GONE
                }else Tools().alertDialog(requireContext(), "${getString(R.string.videoDurationError1)} ${LocalStorage(requireContext()).getMaxPostVideoDuration().toLong() / 60} ${getString(R.string.videoDurationError2)}")
            }
        }catch (e: Exception){}
    }

}