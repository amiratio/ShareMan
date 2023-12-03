package com.application.shareman.service

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.application.shareman.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.utsman.samplegooglemapsdirection.kotlin.model.DirectionResponses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.Exception

class DrawMapRoute(val context: Context, private val map: GoogleMap, private val startPoint: LatLng, private val endPoint: LatLng) {

    private val distance= MutableLiveData<String>()
    private val time= MutableLiveData<String>()

    /*fun draw(){
        val url= "https://maps.googleapis.com/maps/api/directions/json?origin=${startPoint.latitude},${startPoint.longitude}&destination=${endPoint.latitude},${endPoint.longitude}&key=AIzaSyCvxKaE92HxvKvHCkm4cEJS_2SEfrUCffw"
        val queue = Volley.newRequestQueue(context)
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                val responseObject = Gson().fromJson(response.toString(), DirectionResponses::class.java)
                drawPolyline(responseObject)
            },
            {error -> error.printStackTrace() })
        queue.add(stringRequest)
    }*/

    fun drawRoute(){
        val apiServices = RetrofitClient.apiServices()
        apiServices.getDirection("${startPoint.latitude},${startPoint.longitude}", "${endPoint.latitude},${endPoint.longitude}", context.getString(R.string.api_key))
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                    try{
                        distance.value= response.body()!!.routes!![0]!!.legs!![0]!!.distance!!.text!!.replace("m", "M").replace("km", "KM")
                        time.value= response.body()!!.routes!![0]!!.legs!![0]!!.duration!!.text!!.replace("hours", "saat").replace("mins", "dakika")
                            .replace("days", "gün").replace("secs", "saniye")
                        drawPolyline(response)
                    }catch (e: Exception){
                        distance.value= "Tanımsız"
                        time.value= "Tanımsız"
                    }
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                }
            })
    }

    private fun drawPolyline(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(context.resources.getColor(R.color.color4))
        map.addPolyline(polyline)
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(@Query("origin") origin: String,
                         @Query("destination") destination: String,
                         @Query("key") apiKey: String): Call<DirectionResponses>
    }

    private object RetrofitClient {
        fun apiServices(): ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://maps.googleapis.com/")
                .build()

            return retrofit.create(ApiServices::class.java)
        }
    }

    fun getDistance(): MutableLiveData<String>{
        return distance
    }

    fun getTime(): MutableLiveData<String>{
        return time
    }
}