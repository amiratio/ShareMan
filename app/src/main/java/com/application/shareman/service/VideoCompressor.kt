package com.application.shareman.service

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import com.iceteck.silicompressorr.SiliCompressor
import java.io.File
import java.net.URISyntaxException

class VideoCompressor(private val context: Context, private val uri: Uri) {

    val result: SingleLiveEvent<Uri> by lazy { SingleLiveEvent<Uri>() }

    init {
        val file= File(Environment.getExternalStorageDirectory().path + "compressedVideo.mp4")
        CompressVideo().execute("false", uri.toString(), file.path)
    }

    inner class CompressVideo : AsyncTask<String?, String?, String?>() {
        override fun doInBackground(vararg strings: String?): String? {
            var videoPath: String? = null
            try {
                videoPath= SiliCompressor.with(context).compressVideo(Uri.parse(strings[1]), strings[2])
            }catch(e: URISyntaxException) {
                e.printStackTrace()
            }
            return videoPath
        }
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            val file = File(s)
            result.postValue(Uri.fromFile(file))
        }
    }

}