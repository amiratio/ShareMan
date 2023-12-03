package com.application.shareman.service.cache

import android.net.Uri
import com.google.android.gms.maps.model.LatLng

object PostPublishCache {

    var uri: Uri?= null
    var content= ""
    var type= ""
    var subject= ""
    var location: LatLng?= null
    var comments= true

}