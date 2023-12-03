package com.application.shareman.ui.dialog

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.application.shareman.R
import com.application.shareman.service.SingleLiveEvent
import com.application.shareman.service.Tools
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class SelectLocation(private var lat: String?= "", private var lon: String?= "") : DialogFragment(), OnMapReadyCallback {

    val result: SingleLiveEvent<LatLng> by lazy { SingleLiveEvent<LatLng>() }
    private var mapFragment: SupportMapFragment? = null
    private lateinit var map: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view= inflater.inflate(R.layout.dialog_select_location, container, false)
        val window: Window? = dialog!!.window
        val wlp: WindowManager.LayoutParams = window!!.attributes
        //dialog!!.window?.attributes?.windowAnimations = R.style.DialogAnimation
        wlp.gravity = Gravity.CENTER
        dialog!!.setCancelable(true)
        dialog!!.setCanceledOnTouchOutside(false)
        window.attributes = wlp

        uiSetup(view)

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog!!.window?.setLayout(resources.displayMetrics.widthPixels, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private fun uiSetup(view: View){
        val ok= view.findViewById<Button>(R.id.ok)

        Tools().locationRequest(requireActivity())

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        ok.setOnClickListener{
            if(lat!!.isNotBlank() && lon!!.isNotBlank()){
                result.postValue(LatLng(lat!!.toDouble(), lon!!.toDouble()))
                dialog!!.dismiss()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map= p0
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return }
        map.isMyLocationEnabled= true

        if(lat!!.isNotBlank() && lon!!.isNotBlank()){
            map.setOnMapLoadedCallback {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat!!.toDouble(), lon!!.toDouble()), 15f))
            }
        }

        map.setOnCameraChangeListener{
            lat= it.target.latitude.toString()
            lon= it.target.longitude.toString()
        }

    }
}