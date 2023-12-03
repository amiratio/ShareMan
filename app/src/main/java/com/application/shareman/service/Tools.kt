package com.application.shareman.service

import PathUtil
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.IntentSender
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.format.Time
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.application.shareman.R
import com.application.shareman.ui.dialog.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Tools {

    val alertDialogResult: SingleLiveEvent<Boolean> by lazy { SingleLiveEvent<Boolean>() }
    val getCompressedImagePathResult: SingleLiveEvent<String> by lazy { SingleLiveEvent<String>() }
    val getCompressedImageUriResult: SingleLiveEvent<Uri> by lazy { SingleLiveEvent<Uri>() }

    fun alertDialog(context: Context, title: String, description: String?="", buttonText: String?=""){
        val dialog= AlertDialog(title, description, buttonText)
        dialog.show((Tools().unwrap(context) as AppCompatActivity).supportFragmentManager, "AlertDialog")
        dialog.result.observe(Tools().unwrap(context) as LifecycleOwner){
            alertDialogResult.postValue(it)
        }
    }

    fun backgroundStatusBar(context: Context, window: Window){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(context, R.color.background)
        //window.decorView.systemUiVisibility = 0
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.navigationBarColor = ContextCompat.getColor(context, R.color.background)
    }

    fun unwrap(context: Context): Activity? {
        var context= context
        while (context !is Activity && context is ContextWrapper) {
            context = context.baseContext
        }
        return context as Activity?
    }

    fun appcompatContext(context: Context): Activity {
        var context= context
        while (context !is Activity && context is ContextWrapper) {
            context = context.baseContext
        }
        return context as AppCompatActivity
    }

    fun emailMatcher(email: String): Boolean{
        val emailPattern = Pattern.compile("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")
        val emailMatcher = emailPattern.matcher(email)
        return emailMatcher.find()
    }

    fun getCompressedImage(context: Context, compressPercentage: Int, path: String){
        val imageUri = Uri.fromFile(File(path))
        val filePath= PathUtil.getPath(context, imageUri)!!
        CoroutineScope(Dispatchers.IO).launch {
            val compressedImageFile = Compressor.compress(context, File(filePath))
            (Tools().unwrap(context) as AppCompatActivity).runOnUiThread {
                Glide.with(context).asBitmap().load(compressedImageFile).into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap?>?) {
                        val bitmap= Tools().rotateImageIfRequired(context, resource, imageUri)
                        val uri= Tools().getImageUri(context, bitmap!!, 20)!!
                        getCompressedImageUriResult.postValue(uri)
                        getCompressedImagePathResult.postValue(PathUtil.getPath(context, uri))
                    }
                })
            }
        }
    }

    @Throws(IOException::class)
    fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap? {
        val input: InputStream = context.contentResolver.openInputStream(selectedImage)!!
        val ei= if (Build.VERSION.SDK_INT > 23) ExifInterface(input) else ExifInterface(selectedImage.path!!)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(img, 0)
            ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(img, 270)
            else -> img
        }
    }

    fun getImageUri(inContext: Context, inImage: Bitmap, compressPercentage: Int): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, compressPercentage, bytes)
        val path = MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, System.currentTimeMillis().toString(), null)
        return Uri.parse(path)
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val cellular = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return wifi != null && wifi.isConnected || cellular != null && cellular.isConnected
    }

    fun locationRequest(activity: Activity) {
        val mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval((10 * 1000).toLong()).setFastestInterval((1 * 1000).toLong())
        val settingsBuilder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        settingsBuilder.setAlwaysShow(true)
        val result= LocationServices.getSettingsClient(activity).checkLocationSettings(settingsBuilder.build())
        result.addOnCompleteListener { task ->
            try { task.getResult(ApiException::class.java) }
            catch (ex: ApiException) {
                when (ex.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try { val resolvableApiException = ex as ResolvableApiException; resolvableApiException.startResolutionForResult(activity, 3) }
                    catch (e: IntentSender.SendIntentException) { }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { Toast.makeText(activity, activity.getString(R.string.turnOnLocationService), Toast.LENGTH_SHORT).show() }
                }
            }
        }
    }

    fun todayDate(): String{
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        return df.format(Calendar.getInstance().time)
    }

    fun localToUTC(time: Long): Long {
        try {
            val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val date = Date(time)
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val strDate: String = dateFormat.format(date)
            //            System.out.println("Local Millis * " + date.getTime() + "  ---UTC time  " + strDate);//correct
            val dateFormatLocal = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            val utcDate: Date = dateFormatLocal.parse(strDate)
            //            System.out.println("UTC Millis * " + utcDate.getTime() + " ------  " + dateFormatLocal.format(utcDate));
            return utcDate.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return time
    }

    fun utcToLocal(utcTime: Long): Long {
        try {
            val timeFormat = Time()
            timeFormat.set(utcTime + TimeZone.getDefault().getOffset(utcTime))
            return timeFormat.toMillis(true)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return utcTime
    }

    fun timeStampToDate(millis: Long): String{
        val formatter = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return formatter.format(Date(millis))
    }

    fun createTimeStamp(): String{
        return localToUTC(System.currentTimeMillis()).toString()
    }

    fun timeStampToTimeAgo(context: Context, key: String): String{
        return DateToTimeAgoConverter(context).covertTimeToText(timeStampToDate(utcToLocal(key.toLong())))
    }

}